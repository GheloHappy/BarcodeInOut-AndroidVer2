package com.monheim.barcode_inout_v2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.Inventory.InventoryFunctions;
import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;

import java.util.List;
import java.util.Map;

import LocalDb.User;
import LocalDb.UserDbHelper;
import MssqlCon.Login;

public class LoginActivity extends AppCompatActivity {
    ConnectionFragment conFrag = new ConnectionFragment();
    boolean toggle = true;
    boolean offlineToggle = false;

    private UserDbHelper userDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_login);

        userDbHelper = new UserDbHelper(this);

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

        getOnBackPressedDispatcher().addCallback(this, callback);

        btnLogin.setOnClickListener(v -> {
            String userName = etUser.getText().toString();
            String pass = etPass.getText().toString();

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            Login login = new Login();

            if(offlineToggle) {
                if(userDbHelper.getUser(userName)){
                    System.out.println("user exist");

                } else {
                    if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (login.CheckUser(userName, pass)) {
                            List<Map<String, String>> dataList;
                            dataList = login.GetUserDetails(userName);

                            for (Map<String, String> userData : dataList) {
                                int id = Integer.parseInt(userData.get("id"));
                                String username = userData.get("username");
                                String password = userData.get("password");
                                String name = userData.get("name");
                                String department = userData.get("department");

                                User user = new User(id, username, password, name, department);
                                userDbHelper.insertUser(user);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Username/Password or Saved Connection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Connect to Local Wifi for sync", Toast.LENGTH_SHORT).show();
                    }
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
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please check if you are connected to wifi.", Toast.LENGTH_SHORT).show();
                }
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

        //Offline functions
        SwitchMaterial toggleOfflineMode = findViewById(R.id.toggleOfflineMode);
        TextView txtToggleOffline = findViewById(R.id.txtToggleOffline);

        toggleOfflineMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtToggleOffline.setTextColor(Color.RED);
                txtToggleOffline.setText("OFFLINE MODE");
                offlineToggle = true;
            } else {
                txtToggleOffline.setTextColor(Color.GREEN);
                txtToggleOffline.setText("ONLINE MODE");
                offlineToggle = false;
            }
        });


    }
}