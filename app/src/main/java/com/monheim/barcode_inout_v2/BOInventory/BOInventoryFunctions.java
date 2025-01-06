package com.monheim.barcode_inout_v2.BOInventory;

import android.graphics.Color;
import android.util.Log;
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

public class BOInventoryFunctions extends SqlCon {
    Connection con = SQLConnection();
    PublicVars pubVar = new PublicVars();
    String user = pubVar.GetUser();

    public void InsertTempBarcode(String barcode, String uom, int qty, String solomonID) {
        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_tempBOInventoryTrans VALUES ('"+barcode+ "','" + solomonID +"','" + uom +"','" + qty + "','" + user + "')";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean InsertInventory(String refNbr, String remarks) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = dfDate.format(c);
        String currentTime = dfTime.format(c);

        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_BOInventoryTrans SELECT barcode,solomonId,uom, qty, '" +
                        currentDate + "','"+ currentTime + "','" + user + "',description,'" + refNbr +"','" + remarks +"' " + "FROM barcodesys_tempBOInventoryTransDetail WHERE username = '"+user+"'";
                Statement st = con.createStatement();
                System.out.println(query);
                st.execute(query);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        //return "Inventory saved successfully";
        return true;
    }

    public String GetUom(String barcode ) {
        String uom = "";
        try {
            if (con != null) {
//                String query = "SELECT DISTINCT solomonID FROM barcodesys_Products WHERE barcode ='" + barcode + "'";
//                Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery(query);
//
//                if (rs.next()) {
//                    barcode = rs.getString(1);
//                } else {
//                    barcode = "NA";
//                }

//                String queryDt = "SELECT DISTINCT UOM FROM barcodesys_Products WHERE barcode = ?";
//                PreparedStatement stDt = con.prepareStatement(queryDt);
//                stDt.setString(1, barcode);
                String queryDt = "SELECT DISTINCT UOM FROM barcodesys_Products WHERE barcode LIKE ?";
                PreparedStatement stDt = con.prepareStatement(queryDt);
                stDt.setString(1, "%" + barcode + "%");
                ResultSet rsDt = stDt.executeQuery();

                List<String[]> rows = new ArrayList<>();
                while (rsDt.next()) {
                    String[] row = new String[2];
                    row[0] = rsDt.getString(1);
                    rows.add(row);
                }

                int rowCount = rows.size();

                if (rowCount > 1) {
                    uom = "multi"; //this multi value is to trigger selections of multiple barcode
                } else {
                    uom = rows.get(0)[0];
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return uom;
    }

    public String GetSolomonID(String barcode) {
        String solomonId = "";
        try {
            if (con != null) {
//                String query = "SELECT DISTINCT solomonID FROM barcodesys_Products WHERE barcode ='" + barcode + "'";
//                Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery(query);
//
//                if (rs.next()) {
//                    barcode = rs.getString(1);
//                } else {
//                    barcode = "NA";
//                }

                String queryDt = "SELECT DISTINCT solomonID FROM barcodesys_Products WHERE barcode = ?";
                PreparedStatement stDt = con.prepareStatement(queryDt);
                stDt.setString(1, barcode);
                ResultSet rsDt = stDt.executeQuery();

                List<String[]> rows = new ArrayList<>();
                while (rsDt.next()) {
                    String[] row = new String[2];
                    row[0] = rsDt.getString(1);
                    rows.add(row);
                }

                int rowCount = rows.size();

                if (rowCount > 1) {
                    solomonId = "multi";
                } else {
                    solomonId = rows.get(0)[0];
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return solomonId;
    }

    public List<Map<String, String>> GetInventoryBarList() {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_tempBOInventoryTransDetail WHERE username = '" +user + "' ORDER BY solomonID ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("barcode", rs.getString("barcode"));
                    dtTempBarTran.put("solomonID", rs.getString("solomonID"));
                    dtTempBarTran.put("description", rs.getString("description"));
                    dtTempBarTran.put("uom", rs.getString("uom"));
                    dtTempBarTran.put("qty", rs.getString("qty"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public boolean DeleteItem(String barcode, String uom, String user) {
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_tempBOInventoryTrans WHERE barcode = '"+ barcode +"' AND username = '"+ user +"' AND UOM = '"+ uom + "' AND username = '"+ user + "'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }
        return true;
    }
    public boolean UpdateItem(String barcode, int qty, String date) {
        try {
            if (con != null) {
                String query = "Update barcodesys_BOInventoryTrans SET qty = '" + qty+ "' WHERE barcode = '"+ barcode +"' AND username = '"+ user +"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return false;
        }
        return true;
    }

    public boolean CheckBarcode(String barcode, String date) {
        try {
            if (con != null) {
                String query = "SELECT barcode FROM barcodesys_tempBOInventoryTrans WHERE barcode = '"+barcode+"' AND date = '"+ date +"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (!rs.next()) {
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return true;
    }

    public void GetToTQtyCs(TextView tvTotCase) {
        try {
            if (con != null) {
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempBOInventoryTransDetail WHERE Uom = 'CS' AND username = '"+user+"'";
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
    public void GetToTQtyPcs(TextView tvTotCase) {
        try {
            if (con != null) {
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempBOInventoryTransDetail WHERE Uom = 'PCS' AND username = '"+user+"'";
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

    public void ClearTempInventory(){
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_tempBOInventoryTrans WHERE username = '"+user+"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "ClearTempTrans");
        }
    }
}
