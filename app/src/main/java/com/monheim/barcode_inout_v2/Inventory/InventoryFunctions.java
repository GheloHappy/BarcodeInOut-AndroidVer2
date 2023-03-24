package com.monheim.barcode_inout_v2.Inventory;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

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

public class InventoryFunctions extends SqlCon {
    Connection con = SQLConnection();
    PublicVars pubVar = new PublicVars();
    String user = pubVar.GetUser();

    public void InsertTempBarcode(String barcode, String uom, int qty) {
        try {
            if (con != null) {
                String query = "INSERT INTO barcodesys_tempInventoryTrans VALUES ('"+barcode+ "','" + GetSolomonID(barcode) +"','" + uom +"','" + qty + "','" + user + "')";
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
                String query = "INSERT INTO barcodesys_InventoryTrans SELECT barcode,solomonId,uom, qty, '" +
                        currentDate + "','"+ currentTime + "','" + user + "',description,'" + refNbr +"','" + remarks +"' " + "FROM barcodesys_tempInventoryTransDetail WHERE username = '"+user+"'";
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

    private String GetSolomonID(String barcode) {
        try {
            if (con != null) {
                String query = "SELECT solomonID FROM barcodesys_Products WHERE barcode ='" + barcode + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    barcode = rs.getString(1);
                } else {
                    barcode = "NA";
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return barcode;
    }

    public List<Map<String, String>> GetInventoryBarList() {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_tempInventoryTransDetail WHERE username = '" +user + "' ORDER BY solomonID ASC";
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

    public boolean DeleteItem(String barcode, String uom) {
        try {
            if (con != null) {
                String query = "DELETE FROM barcodesys_tempInventoryTrans WHERE barcode = '"+ barcode +"' AND username = '"+ user +"' AND UOM = '"+ uom + "'";
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
                String query = "Update barcodesys_InventoryTrans SET qty = '" + qty+ "' WHERE barcode = '"+ barcode +"' AND username = '"+ user +"'";
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
                String query = "SELECT barcode FROM barcodesys_InventoryTrans WHERE barcode = '"+barcode+"' AND date = '"+ date +"'";
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
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempInventoryTransDetail WHERE Uom = 'CS' AND username = '"+user+"'";
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
                String queryCS = "SELECT SUM(qty) as totCs FROM barcodesys_tempInventoryTransDetail WHERE Uom = 'PCS' AND username = '"+user+"'";
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
                String query = "DELETE FROM barcodesys_tempInventoryTrans WHERE username = '"+user+"'";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "ClearTempTrans");
        }
    }
}
