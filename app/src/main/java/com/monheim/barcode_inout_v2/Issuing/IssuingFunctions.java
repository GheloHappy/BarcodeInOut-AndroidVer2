package com.monheim.barcode_inout_v2.Issuing;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import MssqlCon.PublicVars;
import MssqlCon.SqlCon;

public class IssuingFunctions extends  SqlCon{

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
                    query = "SELECT DISTINCT RefNbr FROM barcodesys_IssueInventory WHERE TranDate = '" + date + "' ORDER BY RefNbr ASC";
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
                String query = "SELECT * FROM barcodesys_IssueInventory_Desc WHERE RefNbr = '" + InvcNbr + "' AND TranDate = '" + InvcDate + "' ORDER BY timeStamp DESC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("solomonID", rs.getString("InvtID"));
                    dtTempBarTran.put("description", rs.getString("Descr"));
                    dtTempBarTran.put("uomOg", rs.getString("UnitDesc"));
                    dtTempBarTran.put("qtyOg", rs.getString("Qty" ));
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
//            try {
//                if (con != null) {
//                    String checkDtQuery = "SELECT DISTINCT RefNbr FROM barcodesys_IssueInventory  WHERE TranDate ='" + tranDate + "'";
//                    Statement stCheck = con.createStatement();
//                    ResultSet rs = stCheck.executeQuery(checkDtQuery);
//                    if (!rs.next()) {
//                        String query;
//                        query = "INSERT INTO barcodesys_IssueInventory (TranDate,RefNbr,InvtID,Descr,UnitDesc,Qty) SELECT TranDate,RefNbr,InvtID,Descr,UnitDesc,Qty FROM barcodesys_issuing_api WHERE TranDate ='" + tranDate + "'";
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
                    String checkDtQuery = "SELECT DISTINCT RefNbr FROM barcodesys_IssueInventory WHERE TranDate = ?";
                    PreparedStatement stCheck = con.prepareStatement(checkDtQuery);
                    stCheck.setString(1, tranDate);
                    ResultSet rs = stCheck.executeQuery();

                    // Create a HashSet to store the existing RefNbr values in barcodesys_IssueInventory
                    Set<String> existingRefNbrSet = new HashSet<>();
                    while (rs.next()) {
                        existingRefNbrSet.add(rs.getString("RefNbr"));
                    }

                    // Close the ResultSet and the statement used for checking
                    rs.close();
                    stCheck.close();

                    String query;

                    if (!existingRefNbrSet.isEmpty()) {
                        // Fetch new rows from barcodesys_issuing_api that have RefNbr not present in barcodesys_IssueInventory
                        query = "INSERT INTO barcodesys_IssueInventory (TranDate, RefNbr, InvtID, Descr, UnitDesc, Qty) " +
                                "SELECT TranDate, RefNbr, InvtID, Descr, UnitDesc, Qty FROM barcodesys_issuing_api " +
                                "WHERE TranDate = ? AND RefNbr NOT IN (" + getCommaSeparatedRefNbrList(existingRefNbrSet) + ")";
                    } else {
                        query = "INSERT INTO barcodesys_IssueInventory (TranDate, RefNbr, InvtID, Descr, UnitDesc, Qty) " +
                                "SELECT TranDate, RefNbr, InvtID, Descr, UnitDesc, Qty FROM barcodesys_issuing_api " +
                                "WHERE TranDate = ? ";
                    }
                    PreparedStatement st = con.prepareStatement(query);
                    st.setString(1, tranDate);
                    st.execute();
                    st.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        return  true;
    }

    private String getCommaSeparatedRefNbrList(Set<String> itemSet) {
        StringBuilder itemList = new StringBuilder();
        for (String ref : itemSet) {
            itemList.append("'").append(ref).append("',");
        }
        if (itemList.length() > 0) {
            itemList.setLength(itemList.length() - 1); // Remove the trailing comma
        }
        return itemList.toString();
    }

    public void GetTotCs(String RefNbr, String tranDate, TextView tvTotCs, TextView tvTotCsOut) {
        try {
            if (con != null) {
                String query = "SELECT SUM(Qty) as qty, SUM(qtyOut) as qtyOut FROM barcodesys_IssueInventory WHERE TranDate ='" + tranDate + "' AND RefNbr = '" + RefNbr +
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
                String query = "SELECT SUM(Qty) as qty, SUM(qtyOut) as qtyOut FROM barcodesys_IssueInventory WHERE TranDate ='" + tranDate + "' AND RefNbr = '" + RefNbr +
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
                    String queryDt = "SELECT DISTINCT InvtId, UnitDesc FROM barcodesys_IssueInventory_Desc WHERE barcode = ? AND RefNbr = ? AND UnitDesc = ?";
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
                query = "SELECT Qty,qtyOut FROM barcodesys_IssueInventory_Desc  WHERE TranDate ='" + date + "' AND RefNbr = '" + refNbr +
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
                    query = "UPDATE barcodesys_IssueInventory Set qtyOut = '" + totQty + "', timeStamp = '" +currentDateTime+ "' WHERE TranDate ='" + date + "' AND RefNbr = '" + refNbr +
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
