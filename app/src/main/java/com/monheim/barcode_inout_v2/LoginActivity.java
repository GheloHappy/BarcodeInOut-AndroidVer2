package com.monheim.barcode_inout_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;

import MssqlCon.Login;
import MssqlCon.Logs;
import MssqlCon.PublicVars;

public class LoginActivity extends AppCompatActivity {
    ConnectionFragment conFrag = new ConnectionFragment();
    boolean toggle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnConn = findViewById(R.id.btnConn);
        EditText etUser = findViewById(R.id.edtUserName);
        EditText etPass = findViewById(R.id.edtPassword);

        btnLogin.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            BarcodeInOutFunctions barInOutFunc = new BarcodeInOutFunctions();
            Login login = new Login();

            barInOutFunc.ClearTempTrans();

            if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
                String userName = etUser.getText().toString();
                String pass = etPass.getText().toString();

                if (login.CheckUser(userName, pass)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username/Password or Saved Connection.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
            }
        });

        btnConn.setOnClickListener(v -> {
            if (toggle == true) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frmLogin, conFrag).commit();
                toggle = false;
            } else {
                //getSupportFragmentManager().beginTransaction().replace(R.id.frmLogin,conFrag).commit();
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                toggle = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //closes app to taskbar running
        //super.onBackPressed(); //enable back press
    }
}