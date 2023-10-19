package com.monheim.barcode_inout_v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import LocalDb.Products;
import LocalDb.ProductsDbHelper;
import MssqlCon.Login;
import MssqlCon.OfflineSync;
import MssqlCon.PublicVars;

public class ConnectionFragment extends Fragment {
    private ProductsDbHelper productsDbHelper;

    OfflineSync offlineSync = new OfflineSync(getContext());

    PublicVars pubVars = new PublicVars();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        EditText etServerIp = rootView.findViewById(R.id.etServerIp);
        Button btnConSave = rootView.findViewById(R.id.btnConSave);
        Button btnSync = rootView.findViewById(R.id.btnSync);
        ProgressBar progressBar = rootView.findViewById(R.id.connectionProgBar);

        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.MULTIPLY); // set color of progressbar

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        etServerIp.setText(settings.getString("ip", "0")); //retrieve ip and port

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        productsDbHelper = new ProductsDbHelper(rootView.getContext());


        if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
            btnConSave.setOnClickListener(v -> {
                updateSharedPref(etServerIp, settings);

                Toast.makeText(getActivity(), "Connection Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            });
            //Sync Button
            btnSync.setOnClickListener(v -> {
                progressBar.setVisibility(View.VISIBLE);
                updateSharedPref(etServerIp, settings);

                String warehouse = pubVars.GetWarehouse();
                productsDbHelper.clearProducts(warehouse);
                if (SyncProducts(warehouse)) {
                    Toast.makeText(getActivity(), "Sync Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Toast.makeText(getActivity(), "Sync Failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void updateSharedPref(EditText etServerIp, SharedPreferences settings){

        String ip = etServerIp.getText().toString();

        SharedPreferences.Editor editor = settings.edit(); //saved ip and port
        editor.putString("ip", ip);
        editor.commit();

        pubVars.SetIp(ip);
    }

    private boolean SyncProducts(String warehouse) {
        List<Map<String, String>> dataList;
        List<Products> products = new ArrayList<>();
        try {
            dataList = offlineSync.getOfflineProducts();
            for (Map<String, String> data : dataList) {
                String barcode = data.get("barcode");
                String description = data.get("description");
                String solomonID = data.get("solomonID");
                String uom = data.get("uom");
                String csPkg = data.get("csPkg");
                products.add(new Products(barcode, description, solomonID, uom, Integer.parseInt(csPkg), warehouse));
            }

            productsDbHelper.syncProducts(products);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

