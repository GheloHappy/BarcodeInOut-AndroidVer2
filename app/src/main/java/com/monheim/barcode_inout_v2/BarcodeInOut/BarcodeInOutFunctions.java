package com.monheim.barcode_inout_v2.BarcodeInOut;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.util.Map;

import MssqlCon.SqlCon;

public class BarcodeInOutFunctions extends SqlCon {
    String solomonCode;
    Connection con;

    public boolean GetSapCode(String barcode, TextView etSap, TextView etDesc) {
        con = SQLConnection();

        try {
            if (con != null) {
                String query = "SELECT sapCode, description FROM Products WHERE barcode ='" + barcode + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    etSap.setText(rs.getString(1));
                    etDesc.setText(rs.getString(2));
                    etSap.setTextColor(Color.BLACK);
                } else {
                    etSap.setText("Item not Found");
                    etSap.setTextColor(Color.RED);
                    etDesc.setText("...");
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }

        return true;
    }

    int id = 1;
    public void InsertIn(String barcode, String uom, int qty, String tranType){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = dfDate.format(c);
        String currentTime = dfTime.format(c);

        try {
            if (con != null) {
                String query = "INSERT INTO tempBarcodeTrans VALUES ('"+ id + "','"+ barcode+ "','" + uom + "','" + qty + "','" + currentDate + "','"+ currentTime + "','"+ tranType +"')";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        id++;
    }

    public void ClearTempTrans(){
        con = SQLConnection();
        try {
            if (con != null) {
                String query = "DELETE FROM tempBarcodeTrans";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public List<Map<String, String>> GetTempBarList() {
        List<Map<String, String>> data = null;
        data = new ArrayList<>();
        con = SQLConnection();

        try {
            if (con != null) {
                String query = "SELECT * FROM TempBarcodeTranDetail";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<String, String>();
                    dtTempBarTran.put("id", rs.getString("id"));
                    dtTempBarTran.put("tranType", rs.getString("tranType"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("description", rs.getString("description"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }
}
