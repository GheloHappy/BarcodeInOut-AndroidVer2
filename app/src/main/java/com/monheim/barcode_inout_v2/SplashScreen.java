package com.monheim.barcode_inout_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import MssqlCon.PublicVars;

public class SplashScreen extends AppCompatActivity {
    PublicVars pubVars = new PublicVars();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        String ip = settings.getString("ip", "0");
        String port = settings.getString("port", "0");
        String warehouse = settings.getString("warehouse", "0");

        pubVars.SetIp(ip);
        pubVars.SetPort(port);
        pubVars.SetWarehouse(warehouse);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        }, 2000 );
    }
}
