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
    public boolean GetBarcode(String barcode, TextView tvBar, TextView tvDesc) {
        try {
            if (con != null) {
                String query = "SELECT barcode, description FROM barcodesys_Products WHERE barcode ='" + barcode + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    tvBar.setText(rs.getString(1));
                    tvDesc.setText(rs.getString(2));
                    tvBar.setTextColor(Color.BLACK);
                } else {
                    tvBar.setText("Item not Found!");
                    tvBar.setTextColor(Color.RED);
                    tvDesc.setText("Please check new barcode tab!");
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
    public void InsertIn(String barcode,String uom, int qty, String tranType, String user, String itemDesc, String solomonID){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = dfDate.format(c);
        String currentTime = dfTime.format(c);

        if (CheckTempBarTranData(user) == false) {
            id = 1;
        }

        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_tempBarcodeTrans VALUES ('"+ id + "','"+ barcode+ "','" + itemDesc + "','" + solomonID + "','" + uom + "','"
                        + qty + "','" + currentDate + "','"+ currentTime + "','"+ tranType +"','"+user+"')";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        id++;
    }
    public boolean CheckTempBarTranData(String user) {
        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_tempBarcodeTrans WHERE username = '"+user+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    return true;
                }
                return false; //return false if no data
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }

        return true;
    }
    public boolean CheckMultiBarcode(String barcode) {
        try {
            if (con != null) {
                String query = "SELECT COUNT(*) AS count FROM barcodesys_Products WHERE barcode = '"+barcode+"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count > 1) {
                        return true; // return true if count is greater than 1
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return false;
    }
    public List<Map<String, String>> GetMultiBarcode(String barcode) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_products WHERE barcode = '"+ barcode +"' ORDER BY solomonId ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtMultiBarcode = new HashMap<>();
                    dtMultiBarcode.put("barcode", rs.getString("barcode"));
                    dtMultiBarcode.put("description", rs.getString("description"));
                    dtMultiBarcode.put("solomonID", rs.getString("solomonID"));
                    data.add(dtMultiBarcode);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ": GetMultiBarcode");
        }

        return data;
    }

    //Barcode In Out Save Fragment
    public List<Map<String, String>> GetTempBarList(String user) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_TempBarcodeTransDetail WHERE username = '"+ user +"' ORDER BY description ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    //dtTempBarTran.put("id", rs.getString("id"));
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
    public void GetToTQtyCs(TextView tvTotCase, String user) {
        try {
            if (con != null) {
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempBarcodeTrans WHERE Uom = 'CS' AND username = '"+user+"'";
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
    public void GetToTQtyPcs(TextView tvTotCase, String user) {
        try {
            if (con != null) {
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempBarcodeTrans WHERE Uom = 'PCS' AND username = '"+user+"'";
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
                String query = "INSERT INTO barcodesys_BarcodeTrans SELECT barcode,description,solomonID,uom, qty, date, date_entry,tranType,'"+ refnbr + "','" + remarks + "',username FROM barcodesys_tempBarcodeTrans";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " InsertRefNbr");
            return false;
        }
        return true;
    }
    public void DeleteTempBarcodeItem(int id, String user){
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_tempBarcodeTrans WHERE id = '"+ id +"' AND username = '"+ user +"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " DeleteTempBarcodeItem");
        }
    }
    public void ClearTempTrans(String user){
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_tempBarcodeTrans WHERE username = '"+user+"'";
                Statement st = con.createStatement();
                st.execute(query);
                System.out.println(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "ClearTempTrans");
        }
    }
}
