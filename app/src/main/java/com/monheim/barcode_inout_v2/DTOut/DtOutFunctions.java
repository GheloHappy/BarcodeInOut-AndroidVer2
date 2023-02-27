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

import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class DtOutFunctions {
    SqlCon sqlCon = new SqlCon();
    PublicVars pubVars = new PublicVars();
    Connection con = sqlCon.SQLConnection();
    public ArrayList<String> GetDt(String date) {
        ArrayList<String> data = new ArrayList<>();
        try {
            if (con != null) {
                String query = "SELECT DISTINCT dt schedDate FROM barcodesys_DTInventory WHERE schedDate = '" + date + "' ORDER BY DT ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    String dt = rs.getString(1);
                    data.add(dt);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public List<Map<String, String>> GetDTList(String dt, String schedDate) {
        List<Map<String, String>> data;
        data = new ArrayList<>();
        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_DTInventory_Desc WHERE dt = '" + dt + "' AND schedDate = '" + schedDate + "' ORDER BY timeStamp DESC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("description", rs.getString("description"));
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

        SyncDT(dt, schedDate); //Insert data to DTInventory like the process of api
        return data;
    }

    public void GetTotCs(String dt, String schedDate,TextView tvTotCs) {
        try {
            if (con != null) {
                String query = "SELECT SUM(qty) as qty FROM barcodesys_DTInventory WHERE schedDate ='" + schedDate + "' AND dt = '" + dt + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotCs.setText(rs.getString(1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void GetTotCsOut(String dt, String schedDate,TextView tvTotCsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(qtyOut) as qty FROM barcodesys_DTInventory WHERE schedDate ='" + schedDate + "' AND dt = '" + dt + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotCsOut.setText(rs.getString(1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String GetSolomonID(String val) {
        try {
            if (con != null) {
                String query = "SELECT solomonID FROM barcodesys_Products WHERE barcode = '" + val + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    val = rs.getString(1);
                } else {
                    val = "NA";
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return val;
    }

    String date, dt, solomonID;
    String warehouse = pubVars.GetWarehouse();
    public boolean UpdateDtItem(int qty) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dfTime.format(c);

        int totQty = outQty + qty;
        if (totQty <= maxQty) {
            try {
                if (con != null) {
                    String query;
                    if(warehouse.equals("Cabrera")) {
                        query = "UPDATE barcodesys_DTInventory Set qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE schedDate ='" + date + "' AND dt = '" + dt + "' AND solomonID ='" + solomonID + "'";
                    } else {
                        query = "UPDATE DTInventory SET qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE schedDate ='" + date + "' AND dt = '" + dt + "' AND solomonID ='" + solomonID + "'";
                    }
                    Statement st = con.createStatement();
                    st.execute(query);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
                String query;
                query = "SELECT qty,qtyOut FROM barcodesys_DTInventory  WHERE schedDate ='" + date + "' AND dt = '" + dt + "' AND solomonID ='" + solomonID + "'";
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
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    private void SyncDT(String dt, String date){
        try {
            if (con != null) {
                String checkDtQuery = "SELECT DISTINCT dt FROM DTInventory  WHERE schedDate ='" + date + "' AND dt = '" + dt + "'";
                Statement stCheck = con.createStatement();
                ResultSet rs = stCheck.executeQuery(checkDtQuery);
                if (!rs.next()) {
                    String query;
                    query = "INSERT INTO DTInventory (schedDate,dt,solomonID,qtyOut) SELECT OrdDate, ShipviaID,InvtID,0 FROM barcodesys_summary_of_delivery_api WHERE OrdDate ='" + date + "' AND ShipviaID = '" + dt + "'";
                    Statement st = con.createStatement();
                    st.execute(query);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
