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
    Connection con = SQLConnection();

    //Barcode In Out Fragment
    public boolean GetBarcode(String barcode, TextView etBar, TextView etDesc) {
        try {
            if (con != null) {
                String query = "SELECT barcode, description FROM barcodesys_Products WHERE barcode ='" + barcode + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    etBar.setText(rs.getString(1));
                    etDesc.setText(rs.getString(2));
                    etBar.setTextColor(Color.BLACK);
                } else {
                    etBar.setText("Item not Found!");
                    etBar.setTextColor(Color.RED);
                    etDesc.setText("Please check new barcode tab!");
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

        if (CheckTempBarTranData() == false) {
            id = 1;
        }

        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_tempBarcodeTrans VALUES ('"+ id + "','"+ barcode+ "','" + uom + "','" + qty + "','" + currentDate + "','"+ currentTime + "','"+ tranType +"')";
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
                String query = "DELETE FROM barcodesys_tempBarcodeTrans";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
    public boolean CheckTempBarTranData() {
        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_TempBarcodeTranDetail";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (!rs.next()) {
                    return false; //return false if no data
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }

        return true;
    }

    //Barcode In Out Save Fragment
    public List<Map<String, String>> GetTempBarList() {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_TempBarcodeTranDetail ORDER BY id ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("id", rs.getString("id"));
                    dtTempBarTran.put("tranType", rs.getString("tranType"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("description", rs.getString("description"));
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
    public void GetToTQty(TextView tvTotCase) {
        try {
            if (con != null) {
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempBarcodeTrans WHERE Uom = 'CS'";
                Statement st = con.createStatement();
                ResultSet rsCS = st.executeQuery(queryCS);

                if (rsCS.next()) {
                    tvTotCase.setText(rsCS.getString(1));
                } else {
                    tvTotCase.setText("n/a");
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
    public boolean InsertRefNbr(String refnbr,String remarks) {
        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_BarcodeTrans SELECT barcode,uom, qty, date, date_entry,tranType,'"+ refnbr + "','" + remarks + "' FROM barcodesys_tempBarcodeTrans";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public void DeleteTempBarcodeItem(int id){
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_tempBarcodeTrans WHERE id = '"+ id +"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}
