package com.monheim.barcode_inout_v2.ViewInventory;

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

public class ViewInventoryFunctions extends SqlCon {
    Connection con = SQLConnection();

    public List<Map<String, String>> GetInvtList(String refNbr, String user) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_InventoryTrans_Report WHERE refNbr = '"+refNbr+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
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

    public boolean GetTotCs(TextView tvTotCs, TextView tvTotPcs, String refNbr, String user) {
        try {
            if (con != null) {
                String query = "SELECT " +
                        " SUM(CASE WHEN uom = 'CS' THEN qty ELSE 0 END) as totCs," +
                        " SUM(CASE WHEN uom = 'PCS' THEN qty ELSE 0 END) as totPcs FROM barcodesys_InventoryTrans_Report" +
                        " WHERE refNbr = '"+refNbr+"' username = '"+user+"' ";
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
}
