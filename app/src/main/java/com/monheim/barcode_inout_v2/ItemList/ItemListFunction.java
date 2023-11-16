package com.monheim.barcode_inout_v2.ItemList;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MssqlCon.SqlCon;

public class ItemListFunction extends SqlCon {
    Connection con = SQLConnection();

    public List<Map<String, String>> GetItemList() {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_productslist ORDER BY description ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("description", rs.getString("description"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }

    public List<Map<String, String>> SearchItem(String searchText) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_productslist WHERE solomonID LIKE '%"+searchText+"%' OR description LIKE '%"+searchText+"%' ORDER BY description ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("description", rs.getString("description"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }

    public boolean RemoveItem(String barcode, String solomonID, String uom){
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_Products WHERE barcode = '"+ barcode +"' AND solomonID = '"+
                        solomonID +"' AND uom = '"+ uom + "'";
                Statement st = con.createStatement();
                st.execute(query);
                System.out.println(query);
            }

        } catch (Exception e) {
            Log.e("Error", "VoidRemoveItem - " + e.getMessage());
            return false;
        }
        return true;
    }

}
