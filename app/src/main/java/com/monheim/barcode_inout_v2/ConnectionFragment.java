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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import LocalDb.Products;
import LocalDb.ProductsDbHelper;
import LocalDb.Users;
import LocalDb.UsersDbHelper;
import MssqlCon.OfflineSync;
import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class ConnectionFragment extends Fragment {
    private ProductsDbHelper productsDbHelper;
    private UsersDbHelper usersDbHelper;
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

        productsDbHelper = new ProductsDbHelper(getContext());
        usersDbHelper = new UsersDbHelper(getContext());

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
                SqlCon sqlCon = new SqlCon(); //refresh sqlcon get IP
                sqlCon.Reconnect();

                String warehouse = pubVars.GetWarehouse();

                new Thread(() -> {
                    boolean productsSynced = SyncProducts(warehouse);
                    boolean usersSynced = SyncUsers(warehouse);

                    // Update the UI on the main thread
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (usersSynced && productsSynced) {
                            Toast.makeText(getActivity(), "Sync Done", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        } else {
                            Toast.makeText(getActivity(), "Sync Failed / Invalid server Connection", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }).start();
            });
        } else {
            Toast.makeText(getActivity(), "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void updateSharedPref(EditText etServerIp, SharedPreferences settings) {

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
            if (!dataList.isEmpty()) {
                for (Map<String, String> data : dataList) {
                    String barcode = data.get("barcode");
                    String description = data.get("description");
                    String solomonID = data.get("solomonID");
                    String uom = data.get("uom");
                    String csPkg = data.get("csPkg");
                    products.add(new Products(barcode, description, solomonID, uom, Integer.parseInt(csPkg), warehouse));
                }
//                productsDbHelper.clearProducts(warehouse);
                productsDbHelper.clearProducts(); //clear products first
                productsDbHelper.syncProducts(products);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean SyncUsers(String warehouse) {
        List <Map<String, String>> dataList;
        List<Users> users = new ArrayList<>();

        try {
            dataList = offlineSync.getOfflineUsers();
            for (Map<String, String> data : dataList) {
                int id = Integer.parseInt(data.get("id"));
                String username = data.get("username");
                String password = data.get("password");
                String name = data.get("name");
                String department = data.get("department");
                users.add(new Users(id,username, password, name, department, warehouse));
            }
//            usersDbHelper.clearUser(warehouse);
            usersDbHelper.clearUser();
            usersDbHelper.syncUsers(users);

        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

        return  true;
    }
}

