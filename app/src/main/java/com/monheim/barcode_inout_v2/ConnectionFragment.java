package com.monheim.barcode_inout_v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import LocalDb.Products;
import LocalDb.ProductsDbHelper;
import LocalDb.UserDbHelper;
import MssqlCon.Login;
import MssqlCon.OfflineSync;
import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class ConnectionFragment extends Fragment {

    private UserDbHelper userDbHelper;
    private ProductsDbHelper productsDbHelper;

    OfflineSync offlineSync = new OfflineSync(getContext());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        EditText etServerIp = rootView.findViewById(R.id.etServerIp);
        EditText etPort = rootView.findViewById(R.id.etPort);
        Button btnConSave = rootView.findViewById(R.id.btnConSave);
        Button btnSync = rootView.findViewById(R.id.btnSync);
        Spinner spinWarehouse = rootView.findViewById(R.id.spinWarehouse);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        etServerIp.setText(settings.getString("ip", "0")); //retrieve ip and port
        etPort.setText(settings.getString("port", "0"));

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        Login login = new Login(rootView.getContext());
        OfflineSync offlineSync = new OfflineSync(rootView.getContext());

        btnConSave.setOnClickListener(v -> {
            String ip = etServerIp.getText().toString();
            String port = etPort.getText().toString();
            String warehouse = spinWarehouse.getSelectedItem().toString();


            if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
                if (login.CheckUser("admin", "adminx") == false) { //test Login
                    Toast.makeText(getActivity(), "Failed to Connect in "+ ip + " - "+ port, Toast.LENGTH_SHORT).show();
                } else  {
                    Toast.makeText(getActivity(), "Connection Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            } else {
                Toast.makeText(getActivity(), "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSync.setOnClickListener(v -> {
            String ip = etServerIp.getText().toString();
            String port = etPort.getText().toString();
            String warehouse = spinWarehouse.getSelectedItem().toString();

            if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {

                if (login.CheckUser("admin", "adminx") == false) { //test Login
                    Toast.makeText(getActivity(), "Failed to Connect in "+ ip + " - "+ port, Toast.LENGTH_SHORT).show();
                } else  {
                    try {
                        SyncProducts(warehouse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            } else {
                Toast.makeText(getActivity(), "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

        private void SyncProducts(String warehouse) {
        List<Map<String, String>> dataList;
        List<Products> products = new ArrayList<>();

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
    }
}