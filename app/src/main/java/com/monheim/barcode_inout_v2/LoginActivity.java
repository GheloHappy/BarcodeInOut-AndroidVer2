package com.monheim.barcode_inout_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;

import MssqlCon.Login;
import MssqlCon.Logs;

public class LoginActivity extends AppCompatActivity {
    BarcodeInOutFunctions barInOutFunc = new BarcodeInOutFunctions();
    Logs log = new Logs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_login);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        EditText etUser = (EditText) findViewById(R.id.edtUserName);
        EditText etPass = (EditText) findViewById(R.id.edtPassword);

        Login login = new Login();
        barInOutFunc.ClearTempTrans();

        btnLogin.setOnClickListener(v -> {
            ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI)
            {
                String userName = etUser.getText().toString();
                String pass = etPass.getText().toString();

                if (login.CheckUser(userName, pass)) {
                    log.InsertUserLog("Login","");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}