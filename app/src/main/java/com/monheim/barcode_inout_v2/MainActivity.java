package com.monheim.barcode_inout_v2;

import androidx.activity.OnBackPressedCallback;
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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.monheim.barcode_inout_v2.BOInventory.BOInventoryFragment;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInFragment;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeOutFragment;
import com.monheim.barcode_inout_v2.DTOut.DtOutFragment;
import com.monheim.barcode_inout_v2.Home.HomeFragment;
import com.monheim.barcode_inout_v2.Inventory.InventoryFragment;
import com.monheim.barcode_inout_v2.Issuing.IssuingFragment;
import com.monheim.barcode_inout_v2.Issuing.IssuingFunctions;
import com.monheim.barcode_inout_v2.ItemList.ItemListFragment;
import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFragment;
import com.monheim.barcode_inout_v2.OS.OSFragment;
import com.monheim.barcode_inout_v2.Van.VanFragment;
import com.monheim.barcode_inout_v2.ViewBOInventory.ViewBOInventoryFragment;
import com.monheim.barcode_inout_v2.ViewInventory.ViewInventoryFragment;

import MssqlCon.Logs;
import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class MainActivity extends AppCompatActivity {
    SqlCon sqlCon = new SqlCon();
//    PublicVars pubVars = new PublicVars();
    Logs log = new Logs();
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
            System.out.println(e.getMessage());
        }
        setContentView(R.layout.activity_main);

        HomeFragment homeFragment = new HomeFragment();
        BarcodeInFragment barcodeInFragment = new BarcodeInFragment();
        BarcodeOutFragment barcodeOutFragment = new BarcodeOutFragment();
        DtOutFragment dtOutFrag = new DtOutFragment();
        VanFragment vanFrag = new VanFragment();
        OSFragment osFrag = new OSFragment();
        IssuingFragment issueFrag = new IssuingFragment();
        NewBarcodeFragment nbarFrag = new NewBarcodeFragment();
        InventoryFragment invtFrag = new InventoryFragment();
        BOInventoryFragment boinvtFrag = new BOInventoryFragment();
        ViewInventoryFragment viewInvtFrag = new ViewInventoryFragment();
        ViewBOInventoryFragment viewBOInvtFrag = new ViewBOInventoryFragment();
        ItemListFragment itemListFrag = new ItemListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        };

        // Add the callback to the back button dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvWarehouse = navigationView.getHeaderView(0).findViewById(R.id.tvWarehouse);
        String loginDetails = "Warehouse Barcode System : " + MssqlCon.PublicVars.GetWarehouse() + " : " +MssqlCon.PublicVars.GetUser();  //set text to view database using
        tvWarehouse.setText(loginDetails);

        navigationView.setNavigationItemSelectedListener(item -> {
            sqlCon.Reconnect();
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
                case R.id.vanOut:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,vanFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.osOut:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,osFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.issuingOut:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,issueFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.newBarcode:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,nbarFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.inventory:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,invtFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.boinventory:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,boinvtFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.viewInventory:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,viewInvtFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.viewBOInventory:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,viewBOInvtFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.itemList:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,itemListFrag).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.logOut:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    log.InsertUserLog("Logout", "");
                    finish();
                    break;
            }
            MssqlCon.PublicVars.SetMainNav(navigationView); //sending naviagtion view to public variable for disabling/enabling menu items after barcode in/out save

            return false;
        });
    }
}