package com.monheim.barcode_inout_v2;

import androidx.activity.OnBackPressedCallback;
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
import com.monheim.barcode_inout_v2.Inventory.InventoryFunctions;
import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;

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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Disable back button press
            }
        };

        // Add the callback to the back button dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        btnLogin.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            Login login = new Login();

            if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
                String userName = etUser.getText().toString();
                String pass = etPass.getText().toString();

                if (login.CheckUser(userName, pass)) {

                    BarcodeInOutFunctions barInOutFunc = new BarcodeInOutFunctions();
                    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();
                    InventoryFunctions invtFunc = new InventoryFunctions();

                    barInOutFunc.ClearTempTrans(userName);
                    newBarFunc.clearUnknownBarcode(userName);
                    invtFunc.ClearTempInventory();

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
}