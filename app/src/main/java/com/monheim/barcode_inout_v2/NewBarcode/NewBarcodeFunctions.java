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
    public void CheckUnknownBarcode(String barcode, String user){
        try {
            if (con != null) {
                String query = "SELECT barcode FROM barcodesys_UnknownBarcodes WHERE barcode = '"+barcode+"' AND username = '"+user+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (!rs.next()) {
                    InsertUnknownBarcode(barcode, user);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
    public void InsertUnknownBarcode(String barcode,String user) {
        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_UnknownBarcodes VALUES ('"+barcode+"', NULL,NULL,NULL,NULL,'"+user+"')";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Map<String, String>> GetUnknownBarList(String user) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT DISTINCT barcode FROM barcodesys_UnknownBarcodes WHERE username =  '"+user+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " GetUnknownBarList");
        }

        return data;
    }

    public boolean InsertToProducts(String barcode, String desc, String solomonId, String uom, int csKpg) {
        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_Products VALUES ('"+barcode+"','"+desc+"','"+solomonId+"','"+uom+"','"+csKpg+"')";
                Statement st = con.createStatement();
                st.execute(query);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " InsertToProducts");
            return false;
        }

        return true;
    }

    public void DeleteUnknownBarcode(String barcode) {
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_UnknownBarcodes WHERE barcode = '"+barcode+"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "DeleteUnknownBarcode");
        }
    }

    public void clearUnknownBarcode(String user) {
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_UnknownBarcodes WHERE username = '"+user+"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "clearUnknownBarcode");
        }
    }
}
