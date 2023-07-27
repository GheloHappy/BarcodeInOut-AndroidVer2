package com.monheim.barcode_inout_v2.DTOut;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class DtOutFunctions {
    SqlCon sqlCon = new SqlCon();
    PublicVars pubVars = new PublicVars();
    Connection con = sqlCon.SQLConnection();
    String warehouse = pubVars.GetWarehouse();
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
                System.out.println(e.getMessage() + " - GET DT");
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
                    dtTempBarTran.put("uomOg", rs.getString("uomOg"));
                    dtTempBarTran.put("qtyOg", rs.getString("qtyOg"));
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
                String query = "SELECT SUM(qtyOg) as qty, SUM(qtyOut) as qtyOut FROM barcodesys_DTInventory_Desc WHERE schedDate ='" + schedDate + "' AND dt = '" + dt +
                        "' AND uomOg = 'CS'";
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
                String query = "SELECT SUM(qtyOg) as qty, SUM(qtyOut) as qtyOut FROM barcodesys_DTInventory_Desc WHERE schedDate ='" + schedDate + "' AND dt = '" + dt +
                        "' AND uomOg = 'PCS'";
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

    public String GetSolomonID(String barcode, String dt, String uom) {
        String solomonId = "";
        try {
            if (con != null) {
                if (CheckSolomonID(barcode)) {
                    String queryDt = "SELECT DISTINCT solomonID, uomOg FROM barcodesys_DTInventory_products WHERE barcode = ? AND dt = ? AND uomOg = ?";
                    PreparedStatement stDt = con.prepareStatement(queryDt);
                    stDt.setString(1, barcode);
                    stDt.setString(2, dt);
                    stDt.setString(3, uom);
                    ResultSet rsDt = stDt.executeQuery();

                    List<String[]> rows = new ArrayList<>();
                    while (rsDt.next()) {
                        String[] row = new String[2];
                        row[0] = rsDt.getString(1);
                        row[1] = rsDt.getString(2);
                        rows.add(row);
                    }

                    int rowCount = rows.size();

                    if (rowCount > 1) {
                        solomonId = "NAITEM";
                    } else {
                        if (rowCount == 1) {
                            solomonId = rows.get(0)[0];
                        } else {
                            solomonId = "NAUOM";
                        }
                    }
                } else {
                    solomonId = "NA";
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return solomonId;
    }

    private boolean CheckSolomonID(String barcode){
        try{
            if(con != null) {
                String query = "SELECT DISTINCT solomonID, uom FROM barcodesys_Products WHERE barcode = ?";
                PreparedStatement st = con.prepareStatement(query);
                st.setString(1, barcode);
                ResultSet rs = st.executeQuery();

                if(rs.next()) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "CheckSolomonID");
        }
        return false;
    }

    String date, dt, solomonID;
    public boolean UpdateDtItem(int qty, String uomOg) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dfTime.format(c);

        int totQty = outQty + qty;
        if (totQty <= maxQty) {
            try {
                if (con != null) {
                    String query;
                    query = "UPDATE barcodesys_DTInventory Set qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE schedDate ='" + date + "' AND dt = '" + dt +
                            "' AND solomonID ='" + solomonID + "' AND uomOg = '" + uomOg +"'";
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
    public boolean GetLastQty(String _date, String _dt, String _solomonID, String uomOg) {
        date = _date;
        dt = _dt;
        solomonID = _solomonID;
        try {
            if (con != null) {
                String query;
                query = "SELECT qtyOg,qtyOut FROM barcodesys_DTInventory_Desc  WHERE schedDate ='" + date + "' AND dt = '" + dt +
                        "' AND solomonID ='" + solomonID + "' AND uomOg ='" + uomOg + "'";
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
//            try {
//                if (con != null) {
//                    String checkDtQuery = "SELECT DISTINCT dt FROM barcodesys_DTInventory  WHERE schedDate ='" + date + "'";
//                    Statement stCheck = con.createStatement();
//                    ResultSet rs = stCheck.executeQuery(checkDtQuery);
//                    if (!rs.next()) {
//                        String query;
//                        query = "INSERT INTO barcodesys_DTInventory (InvcNbr,schedDate,dt,solomonID,uom,qty,CnvFact,uomOg,qtyOg) SELECT InvcNbr,OrdDate,ShipViaID,InvtID,UnitDesc,QtyShip,CnvFact,uomOg,qtyOg FROM barcodesys_CutDt_api2 WHERE OrdDate ='" + date + "'";
//                        Statement st = con.createStatement();
//                        st.execute(query);
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                return  false;
//            }

            try {
                if (con != null) {
                    // Fetch existing dt values from barcodesys_DTInventory for the given date
                    String checkDtQuery = "SELECT DISTINCT dt FROM barcodesys_DTInventory WHERE schedDate = ?";
                    PreparedStatement stCheck = con.prepareStatement(checkDtQuery);
                    stCheck.setString(1, date);
                    ResultSet rs = stCheck.executeQuery();

                    // Create a HashSet to store the existing dt values in barcodesys_DTInventory
                    Set<String> existingDtSet = new HashSet<>();
                    while (rs.next()) {
                        existingDtSet.add(rs.getString("dt"));
                    }

                    // Close the ResultSet and the statement used for checking
                    rs.close();
                    stCheck.close();

                    // Fetch new dt values from barcodesys_CutDt_api2 that are not present in barcodesys_DTInventory
                    String insertQuery = "INSERT INTO barcodesys_DTInventory (InvcNbr, schedDate, dt, solomonID, uom, qty, CnvFact, uomOg, qtyOg) " +
                            "SELECT InvcNbr, OrdDate, ShipViaID, InvtID, UnitDesc, QtyShip, CnvFact, uomOg, qtyOg " +
                            "FROM barcodesys_CutDt_api2 " +
                            "WHERE OrdDate = ? AND shipviaid NOT IN ('" + getCommaSeparatedDtList(existingDtSet) + "')";

                    PreparedStatement stInsert = con.prepareStatement(insertQuery);
                    stInsert.setString(1, date);
                    stInsert.execute();
                    stInsert.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return  true;
    }

    private String getCommaSeparatedDtList(Set<String> dtSet) {
        StringBuilder dtList = new StringBuilder();
        for (String dt : dtSet) {
            dtList.append("'").append(dt).append("',");
        }
        if (dtList.length() > 0) {
            dtList.setLength(dtList.length() - 1); // Remove the trailing comma
        }
        return dtList.toString();
    }
}
