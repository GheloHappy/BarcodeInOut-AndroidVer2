package com.monheim.barcode_inout_v2.Home;

import android.graphics.Color;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MssqlCon.SqlCon;

public class HomeFunctions extends SqlCon {
    Connection con;
    public List<Map<String, String>> GetBarList(String refNbr) {
        List<Map<String, String>> data;
        data = new ArrayList<>();
        con = SQLConnection();

        try {
            if (con != null) {
                String query = "SELECT * FROM BarcodeTranHistory WHERE refNbr = '"+refNbr+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    //dtTempBarTran.put("sapCode", rs.getString("sapCode"));
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
    public boolean GetTotCs(TextView tvTotCs, String refNbr) {
        con = SQLConnection();
        try {
            if (con != null) {
                String query = "SELECT SUM(qty) as totCs FROM BarcodeTranHistory WHERE refNbr = '"+refNbr+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    if (rs.getString(1) != null){
                        tvTotCs.setText(rs.getString(1));
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
