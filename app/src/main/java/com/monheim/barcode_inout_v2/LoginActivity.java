package com.monheim.barcode_inout_v2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.Inventory.InventoryFunctions;
import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;

import LocalDb.UsersDbHelper;
import MssqlCon.Login;
import MssqlCon.PublicVars;

public class LoginActivity extends AppCompatActivity {
    ConnectionFragment conFrag = new ConnectionFragment();
    boolean toggle = true;
    boolean offlineToggle = false;

    private UsersDbHelper userDbHelper;

    Login login = new Login();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_login);

        userDbHelper = new UsersDbHelper(this);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnConn = findViewById(R.id.btnConn);
        EditText etUser = findViewById(R.id.edtUserName);
        EditText etPass = findViewById(R.id.edtPassword);
        ProgressBar progressBar = findViewById(R.id.loginProgBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, android.R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY); // set color of progressbar

        //Offline functions
        SwitchMaterial switchOfflineMode = findViewById(R.id.toggleOfflineMode);
        TextView txtToggleOffline = findViewById(R.id.txtToggleOffline);
//        Spinner spinWarehouse = findViewById(R.id.offlineSpinWarehouse);
//        spinWarehouse.setVisibility(View.INVISIBLE);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Disable back button press
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        btnLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String userName = etUser.getText().toString();
            String pass = etPass.getText().toString();

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            if (offlineToggle) {
                String warehouse = PublicVars.GetWarehouse();
                    if (userDbHelper.localLoginUser(userName, pass)) {
                        PublicVars.SetUser(userName); //set offline user
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
            } else {
                if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {

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
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnConn.setOnClickListener(v -> {
            if (toggle) {
                switchOfflineMode.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frmLogin, conFrag).commit();
                toggle = false;
            } else {
                switchOfflineMode.setVisibility(View.VISIBLE);
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                toggle = true;
            }
        });

        switchOfflineMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtToggleOffline.setTextColor(Color.RED);
                txtToggleOffline.setText("OFFLINE MODE");
                offlineToggle = true;
                btnConn.setVisibility(View.INVISIBLE);
            } else {
                txtToggleOffline.setTextColor(Color.GREEN);
                txtToggleOffline.setText("ONLINE MODE");
                offlineToggle = false;
                btnConn.setVisibility(View.VISIBLE);
            }
        });

    }
}