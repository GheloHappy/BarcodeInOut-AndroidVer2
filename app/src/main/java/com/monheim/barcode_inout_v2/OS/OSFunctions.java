package com.monheim.barcode_inout_v2.OS;

import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class OSFunctions extends SqlCon {

    SqlCon sqlCon = new SqlCon();
    PublicVars pubVars = new PublicVars();
    Connection con = sqlCon.SQLConnection();
    String warehouse = pubVars.GetWarehouse();

    public ArrayList<String> GetOs(String date) {
        ArrayList<String> data = new ArrayList<>();
        if(SyncOs(date)) //Insert data to DTInventory like the process of api
        {
            try {
                if (con != null) {
                    String query;
                    query = "SELECT DISTINCT InvcNbr FROM barcodesys_OsInventory WHERE InvcDate = '" + date + "' ORDER BY InvcNbr ASC";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if (!rs.isBeforeFirst()) {
                        // If the result set is empty, add a message to the list
                        data.add("No data found");
                    } else {
                        // If the result set is not empty, loop through the rows and add them to the list
                        while (rs.next()) {
                            String InvcNbr = rs.getString(1);
                            data.add(InvcNbr);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage() + " - GetVan");
            }
        }
        return data;
    }

    public List<Map<String, String>> GetOsList(String InvcNbr, String InvcDate) {
        List<Map<String, String>> data;
        data = new ArrayList<>();
        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_OsInventory_Desc WHERE InvcNbr = '" + InvcNbr + "' AND InvcDate = '" + InvcDate + "' ORDER BY timeStamp DESC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("InvtID"));
                    dtTempBarTran.put("description", rs.getString("Descr"));
                    dtTempBarTran.put("uomOg", rs.getString("UnitDesc"));
                    dtTempBarTran.put("qtyOg", rs.getString("QtyShip" ));
                    dtTempBarTran.put("qtyOut", rs.getString("qtyOut"));
                    dtTempBarTran.put("timeStamp", rs.getString("timeStamp"));
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "GetVanList");
        }
        return data;
    }
    private boolean SyncOs(String tranDate){
        if(warehouse.equals("Monheim"))
        {
            try {
                if (con != null) {
                    String checkDtQuery = "SELECT DISTINCT InvcNbr FROM barcodesys_OsInventory  WHERE InvcDate ='" + tranDate + "'";
                    Statement stCheck = con.createStatement();
                    ResultSet rs = stCheck.executeQuery(checkDtQuery);
                    if (!rs.next()) {
                        String query;
                        query = "INSERT INTO barcodesys_OsInventory (InvcDate,InvcNbr,InvtID,Descr,UnitDesc,QtyShip) SELECT User9,InvcNbr,InvtID,Descr,UnitDesc,QtyShip FROM barcodesys_summary_of_os_api WHERE User9 ='" + tranDate + "'";
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

    public void GetTotCs(String RefNbr, String tranDate, TextView tvTotCs, TextView tvTotCsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(QtyShip) as qty, SUM(qtyOut) as qtyOut FROM barcodesys_OsInventory WHERE InvcDate ='" + tranDate + "' AND InvcNbr = '" + RefNbr +
                        "' AND UnitDesc = 'CS'";
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
    public void GetTotPcs(String RefNbr, String tranDate,TextView tvTotPcs,TextView tvTotPcsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(QtyShip) as qty, SUM(qtyOut) as qtyOut FROM barcodesys_OsInventory WHERE InvcDate ='" + tranDate + "' AND InvcNbr = '" + RefNbr +
                        "' AND UnitDesc = 'PCS'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if(rs.next()) {
                    tvTotPcs.setText(rs.getString(1));
                    tvTotPcsOut.setText(rs.getString(2));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "GetTotPCs");
        }
    }

    public String GetSolomonID(String barcode, String refnbr, String uom) {
        String solomonId = "";
        try {
            if (con != null) {
                if (CheckSolomonID(barcode)) {
                    String queryDt = "SELECT DISTINCT InvtId, UnitDesc FROM barcodesys_OsInventory_Desc WHERE barcode = ? AND InvcNbr = ? AND UnitDesc = ?";
                    PreparedStatement stDt = con.prepareStatement(queryDt);
                    stDt.setString(1, barcode);
                    stDt.setString(2, refnbr);
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
    String date, refNbr, solomonID;
    int maxQty, outQty;
    public boolean GetLastQty(String _date, String _refNbr, String _solomonID, String uom) {
        date = _date;
        refNbr = _refNbr;
        solomonID = _solomonID;
        try {
            if (con != null) {
                String query;
                query = "SELECT Qty,qtyOut FROM barcodesys_OsnInventory_Desc  WHERE InvcDate ='" + date + "' AND InvcNbr = '" + refNbr +
                        "' AND InvtID ='" + solomonID + "' AND UnitDesc ='" + uom + "'";
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
    public boolean UpdateOsItem(int qty, String uomOg) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dfTime.format(c);

        int totQty = outQty + qty;
        if (totQty <= maxQty) {
            try {
                if (con != null) {
                    String query;
                    query = "UPDATE barcodesys_OsInventory Set qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE InvcDate ='" + date + "' AND InvcNbr = '" + refNbr +
                            "' AND InvtId ='" + solomonID + "' AND UnitDesc = '" + uomOg +"'";
                    Statement st = con.createStatement();
                    st.execute(query);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage() + " UpdateVanItem");
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}