package com.monheim.barcode_inout_v2.BarcodeIn;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import MssqlCon.SqlCon;

public class BarcodeIN extends SqlCon {

    public void GetSapCode(String barcode, TextView etSap, TextView etDesc) {
        Connection con = SQLConnection();
        String sapCode = "";

        try {
            if (con != null) {
                String query = "SELECT sapCode, description FROM Products WHERE barcode ='" + barcode + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    etSap.setText(rs.getString(1));
                    etDesc.setText(rs.getString(2));
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}
