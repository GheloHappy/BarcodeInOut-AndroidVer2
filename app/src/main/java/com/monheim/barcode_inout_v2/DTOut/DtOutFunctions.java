package com.monheim.barcode_inout_v2.DTOut;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import MssqlCon.SqlCon;

public class DtOutFunctions {
    SqlCon sqlCon = new SqlCon();
    Connection con = sqlCon.SQLConnection();
    public ArrayList<String> GetDTDate(String dtDate) {
        ArrayList<String> data = new ArrayList<>();
        try {
            if (con != null) {
                String query = "SELECT DISTINCT schedDate FROM DTInventory WHERE schedDate = '" + dtDate+ "'";
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

    public ArrayList<String> GetDt(String date) {
        ArrayList<String> data = new ArrayList<>();
        try {
            if (con != null) {
                String query = "SELECT DISTINCT dt schedDate FROM DTInventory WHERE schedDate = '" + date + "' ORDER BY DT ASC";
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

    public List<Map<String, String>> GetDTList(String dt, String schedDate) {
        List<Map<String, String>> data;
        data = new ArrayList<>();
        try {
            if (con != null) {
                String query = "SELECT * FROM DTInventory WHERE dt = '" + dt + "' AND schedDate = '" + schedDate + "' ORDER BY timeStamp DESC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    dtTempBarTran.put("qty", rs.getString("qty"));
                    dtTempBarTran.put("qtyOut", rs.getString("qtyOut"));
                    dtTempBarTran.put("timeStamp", rs.getString("timeStamp"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return data;
    }

    public void GetTotCs(String dt, String schedDate,TextView tvTotCs) {
        try {
            if (con != null) {
                String query = "SELECT SUM(qty) as qty FROM DTInventory WHERE schedDate ='" + schedDate + "' AND dt = '" + dt + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotCs.setText(rs.getString(1));
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
    public void GetTotCsOut(String dt, String schedDate,TextView tvTotCsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(qtyOut) as qty FROM DTInventory WHERE schedDate ='" + schedDate + "' AND dt = '" + dt + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotCsOut.setText(rs.getString(1));
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public String GetSolomonID(String val) {
        try {
            if (con != null) {
                String query = "SELECT solomonID FROM Products WHERE barcode = '" + val + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
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

    String date, dt, solomonID;
    public boolean UpdateDtItem(int qty) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dfTime.format(c);

        int totQty = outQty + qty;
        if (totQty <= maxQty) {
            try {
                if (con != null) {
                    String query = "UPDATE DTInventory Set qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE schedDate ='" + date + "' AND dt = '" + dt + "' AND solomonID ='" + solomonID + "'";
                    Statement st = con.createStatement();
                    st.execute(query);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    int maxQty, outQty;
    public boolean GetLastQty(String _date, String _dt, String _solomonID) {
        date = _date;
        dt = _dt;
        solomonID = _solomonID;
        try {
            if (con != null) {
                String query = "SELECT qty,qtyOut FROM DTInventory  WHERE schedDate ='" + date + "' AND dt = '" + dt + "' AND solomonID ='" + solomonID + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    if (rs.getString(2) != null) {
                        maxQty = Integer.parseInt(rs.getString(1));
                        outQty = Integer.parseInt(rs.getString(2));
                    } else {
                        outQty = 0;
                        maxQty = Integer.parseInt(rs.getString(1));
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }
        return true;
    }
}
