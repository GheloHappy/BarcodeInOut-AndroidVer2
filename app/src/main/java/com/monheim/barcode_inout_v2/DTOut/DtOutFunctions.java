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
    String warehouse = pubVars.GetWarehouse();
    String itemUom;
    public ArrayList<String> GetDt(String date) {
        ArrayList<String> data = new ArrayList<>();
        if(SyncDT(date)) //Insert data to DTInventory like the process of api
        {
            try {
                if (con != null) {
                    String query;
                    query = "SELECT DISTINCT dt FROM barcodesys_DTInventory WHERE schedDate = '" + date + "' ORDER BY DT ASC";

                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if (!rs.isBeforeFirst()) {
                        // If the result set is empty, add a message to the list
                        data.add("No data found");
                    } else {
                        // If the result set is not empty, loop through the rows and add them to the list
                        while (rs.next()) {
                            String dt = rs.getString(1);
                            data.add(dt);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage() + "GET DT");
            }
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
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "GET DT LIST");
        }
        return data;
    }

    public void GetTotCs(String dt, String schedDate,TextView tvTotCs,TextView tvTotCsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(qty) as qty, SUM(qtyOut) as qty FROM barcodesys_DTInventory WHERE schedDate ='" + schedDate + "' AND dt = '" + dt + "' AND uom = 'CS'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotCs.setText(rs.getString(1));
                    tvTotCsOut.setText(rs.getString(2));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "GetTotCs");
        }
    }
    public void GetTotPcs(String dt, String schedDate,TextView tvTotPcs,TextView tvTotPcsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(qty) as qty, SUM(qtyOut) as qty FROM barcodesys_DTInventory WHERE schedDate ='" + schedDate + "' AND dt = '" + dt + "' AND uom = 'PCS'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotPcs.setText(rs.getString(1));
                    tvTotPcsOut.setText(rs.getString(2));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "GetTotCs");
        }
    }

    public String GetSolomonID(String val) {
        try {
            if (con != null) {
                String query = "SELECT solomonID,uom FROM barcodesys_Products WHERE barcode = '" + val + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    val = rs.getString(1);
                    itemUom = rs.getString(2); //get item UOM for specific verification of barcode in out
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
    public boolean UpdateDtItem(int qty) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dfTime.format(c);

        int totQty = outQty + qty;
        if (totQty <= maxQty) {
            try {
                if (con != null) {
                    String query;
                    query = "UPDATE barcodesys_DTInventory Set qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE schedDate ='" + date + "' AND dt = '" + dt + "' AND solomonID ='" + solomonID + "'";
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
                query = "SELECT qty,qtyOut FROM barcodesys_DTInventory  WHERE schedDate ='" + date + "' AND dt = '" + dt +
                        "' AND solomonID ='" + solomonID + "' AND uom ='" + itemUom + "'";
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
    private boolean SyncDT(String date){
        if(warehouse.equals("Monheim"))
        {
            try {
                if (con != null) {
                    String checkDtQuery = "SELECT DISTINCT dt FROM barcodesys_DTInventory  WHERE schedDate ='" + date + "'";
                    Statement stCheck = con.createStatement();
                    ResultSet rs = stCheck.executeQuery(checkDtQuery);
                    if (!rs.next()) {
                        String query;
                        query = "INSERT INTO barcodesys_DTInventory (schedDate,dt,solomonID,uom,qty,qtyOut,CnvFact) SELECT OrdDate, ShipviaID,InvtID,UnitDesc,QtyShip,0,CnvFact FROM barcodesys_summary_of_delivery_api WHERE OrdDate ='" + date + "'";
                        Statement st = con.createStatement();
                        st.execute(query);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return  false;
            }
        }
        return  true;
    }
}
