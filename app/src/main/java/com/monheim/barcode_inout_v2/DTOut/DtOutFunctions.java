package com.monheim.barcode_inout_v2.DTOut;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MssqlCon.SqlCon;

public class DtOutFunctions extends SqlCon {
    Connection con;

    public ArrayList<String> GetDTDate(){
        ArrayList<String> data = new ArrayList<>();
        con = SQLConnection();
        try {
            if (con != null) {
                String query = "SELECT DISTINCT schedDate FROM DTInventory";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    String date = rs.getString(1);
                    data.add(date);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }
    public ArrayList<String> GetDt(String date){
        ArrayList<String> data = new ArrayList<>();
        con = SQLConnection();
        try {
            if (con != null) {
                String query = "SELECT DISTINCT dt schedDate FROM DTInventory WHERE schedDate = '"+date+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    String dt = rs.getString(1);
                    data.add(dt);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }
    public List<Map<String, String>> GetDTList(String dt) {
        List<Map<String, String>> data;
        data = new ArrayList<>();
        con = SQLConnection();
        try {
            if (con != null) {
                String query = "SELECT * FROM DTInventory WHERE dt = '"+dt+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    dtTempBarTran.put("qty", rs.getString("qty"));
                    dtTempBarTran.put("qtyOut", rs.getString("qtyOut"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }
    public String GetSolomonID(String val) {
        try {
            con = SQLConnection();
            if (con != null) {
                String query = "SELECT solomonID FROM Products WHERE barcode = '"+val+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                if(rs.next()) {
                    val = rs.getString(1);
                } else {
                    val = "NA";
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return val;
    }
    public void UpdateDtItem(String date, String dt,String solomonID, int qty){
        try {
            con = SQLConnection();
            if (con != null) {
                String query = "UPDATE DTInventory Set qtyOut = '"+qty+"' WHERE schedDate ='"+date+"' AND dt = '"+dt+"' AND solomonID ='"+solomonID+"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}
