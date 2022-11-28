package com.monheim.barcode_inout_v2.NewBarcode;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MssqlCon.SqlCon;

public class NewBarcodeFunctions extends SqlCon {
    Connection con = SQLConnection();

    //Insert unknown barcode - referenced in Barcode IN/Out Fragments
    public void CheckUnknownBarcode(String barcode){
        try {
            if (con != null) {
                String query = "SELECT barcode FROM UnknownBarcodes WHERE barcode = '"+barcode+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (!rs.next()) {
                    InsertUnknownBarcode(barcode);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
    public void InsertUnknownBarcode(String barcode) {
        try {
            if (con != null) {
                String query = "INSERT INTO UnknownBarcodes VALUES ('"+barcode+"', NULL,NULL,NULL,NULL,NULL)";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public List<Map<String, String>> GetUnknownBarList() {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM UnknownBarcodes";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
//                    dtTempBarTran.put("sapCode", rs.getString("sapCode"));
//                    dtTempBarTran.put("description", rs.getString("description"));
//                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
//                    dtTempBarTran.put("uom", rs.getString("uom"));
//                    dtTempBarTran.put("csPkg", rs.getString("csPkg"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }

    public boolean InsertToProducts(String barcode, String sapCode, String desc, String solomonId, String uom, int csKpg) {
        try {
            if (con != null) {
                String query = "INSERT INTO Products VALUES ('"+barcode+"','"+sapCode+"','"+desc+"','"+solomonId+"','"+uom+"','"+csKpg+"')";
                Statement st = con.createStatement();
                st.execute(query);

            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }

        return true;
    }
    public void DeleteUnknownBarcode(String barcode) {
        try {
            if (con != null) {
                String query = "DELETE FROM UnknownBarcodes WHERE barcode = '"+barcode+"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}
