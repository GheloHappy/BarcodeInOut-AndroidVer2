package com.monheim.barcode_inout_v2.ViewBOInventory;

import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MssqlCon.SqlCon;

public class ViewBOInventoryFunctions extends SqlCon {
    Connection con = SQLConnection();

    public List<Map<String, String>> GetInvtList(String refNbr, String user) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_BOInventoryTrans_Report_TabView WHERE refNbr LIKE '%"+refNbr+"%' OR remarks LIKE '%"+refNbr+"%' AND username = '"+user+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("description", rs.getString("description"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    dtTempBarTran.put("qty", rs.getString("qty"));
                    dtTempBarTran.put("remarks", rs.getString("remarks"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }

    public boolean GetTotCs(TextView tvTotCs, TextView tvTotPcs, String refNbr, String user) {
        try {
            if (con != null) {
                String query = "SELECT " +
                        " SUM(CASE WHEN uom = 'CS' THEN qty ELSE 0 END) as totCs," +
                        " SUM(CASE WHEN uom = 'PCS' THEN qty ELSE 0 END) as totPcs FROM barcodesys_BOInventoryTrans_Report" +
                        " WHERE refNbr LIKE '%"+refNbr+"%' OR remarks LIKE '%"+refNbr+"%'  AND username = '"+user+"' ";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    if (rs.getString(1) != null){
                        tvTotCs.setText(rs.getString(1));
                        tvTotPcs.setText(rs.getString(2));
                    }else{
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return true;
    }

    public boolean CheckVoidUser(String pass) {
        try {
            if (con != null) {
                String query = "SELECT password FROM barcodesys_Users WHERE username = 'void' AND password = '"+pass+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    if (rs.getString(1) != null){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            System.out.println("getVoidUser" + e.getMessage());
        }

        return false;
    }

    public boolean VoidRemoveItem(String barcode, String solomonID, String uom, String user, String remarks){
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_BOTrans WHERE barcode = '"+ barcode +"' AND solomonID = '"+
                        solomonID +"' AND uom = '"+ uom + "' AND username = '"+ user + "' AND remarks = '"+ remarks + "'";
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

    public String GetTotItem(String refNbr, String solomonID, String uom) {
        try {
            if (con != null) {
                String query = "SELECT " +
                        " SUM(qty) as tot" +
                        " FROM barcodesys_BOInventoryTrans_Report" +
                        " WHERE refNbr = '"+refNbr+"' AND solomonID = '"+solomonID+"' AND uom = '"+uom+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    if (rs.getString(1) != null){
                        return rs.getString(1);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            System.out.println("GetTotItem" + e.getMessage());
        }
        return "0";
    }
}
