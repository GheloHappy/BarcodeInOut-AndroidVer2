package com.monheim.barcode_inout_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInFragment;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeOutFragment;
import com.monheim.barcode_inout_v2.DTOut.DtOutFragment;
import com.monheim.barcode_inout_v2.Home.HomeFragment;

import MssqlCon.PublicVars;

public class MainActivity extends AppCompatActivity {
    PublicVars pubVars = new PublicVars();

    DrawerLayout drawerLayout;
    public NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_main);

        HomeFragment homeFragment = new HomeFragment();
        BarcodeInFragment barcodeInFragment = new BarcodeInFragment();
        BarcodeOutFragment barcodeOutFragment = new BarcodeOutFragment();
        DtOutFragment dtOutFrag = new DtOutFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.barcodeIn:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,barcodeInFragment).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.barcodeOut:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,barcodeOutFragment).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.dtOut:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,dtOutFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.logOut:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    break;
            }

            pubVars.SetMainNav(navigationView); //sending naviagtion view to public variable for disabling/enabling menu items after barcode save

            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
//        } else {
//            moveTaskToBack(true); //closes app but resets the login
//            super.onBackPressed(); //enable back press
//        }
    }
}