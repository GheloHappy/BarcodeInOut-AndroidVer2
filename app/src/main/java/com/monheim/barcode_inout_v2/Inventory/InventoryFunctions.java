package com.monheim.barcode_inout_v2.Inventory;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class InventoryFunctions extends SqlCon {
    Connection con = SQLConnection();
    PublicVars pubVar = new PublicVars();

    public void InsertBarcode(String barcode, String uom, int qty, String date) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentTime = dfTime.format(c);

        try {
            if (con != null) {
                String query = "INSERT INTO InventoryTrans VALUES ('"+barcode+ "','" + uom +"','" + qty + "','" + date + "','" + currentTime + "','" + pubVar.GetUser() + "')";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public List<Map<String, String>> GetInventoryBarList(String date) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM InventoryTrans WHERE date = '" + date + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println(query);
                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    dtTempBarTran.put("qty", rs.getString("qty"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }

}
