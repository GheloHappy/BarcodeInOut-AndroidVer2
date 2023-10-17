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

import MssqlCon.Login;
import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class ConnectionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        EditText etServerIp = rootView.findViewById(R.id.etServerIp);
        EditText etPort = rootView.findViewById(R.id.etPort);
        Button btnConSave = rootView.findViewById(R.id.btnConSave);
        Spinner spinWarehouse = rootView.findViewById(R.id.spinWarehouse);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        etServerIp.setText(settings.getString("ip", "0")); //retrieve ip and port
        etPort.setText(settings.getString("port", "0"));

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        btnConSave.setOnClickListener(v -> {
            String ip = etServerIp.getText().toString();
            String port = etPort.getText().toString();
            String warehouse = spinWarehouse.getSelectedItem().toString();
            Login login = new Login(rootView.getContext());

            if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {

                SharedPreferences.Editor editor = settings.edit(); //saved ip and port
                editor.putString("ip", ip);
                editor.putString("port", port);
                editor.putString("warehouse", warehouse);
                editor.commit();

                PublicVars pubVars = new PublicVars();
                pubVars.SetIp(ip);
                pubVars.SetPort(port);
                pubVars.SetWarehouse(warehouse);

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

        return rootView;
    }
}