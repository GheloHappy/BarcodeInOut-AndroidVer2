package MssqlCon;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlCon {
    PublicVars pubVars = new PublicVars();
    Connection con;

    @SuppressLint("NewApi")
    public Connection SQLConnection() {
        //String ip="192.168.1.249", port="1433", dbName="BarcodeInOut", un="sa", pass = "Passw0rd"; mdiserver-l
        //String ip="192.168.1.252", port="1433", dbName="MONHEIMAPP", un="sa", pass = "Passw0rd"; mdiserver
        //String ip="192.168.1.248", port="1433", dbName="MLDIAPP", un="sa", pass = "Passw0rd"; solomon
        String ip, port, dbName, un, pass, warehouse;

        warehouse = pubVars.GetWarehouse();
        if(warehouse.equals("Cabrera")) {
            ip= pubVars.GetIp(); port=pubVars.GetPort(); dbName="BarcodeInOut"; un="sa"; pass = "Passw0rd";
        } else if (warehouse.equals("Cebu")) {
            ip= pubVars.GetIp(); port=pubVars.GetPort(); dbName="BarcodeInOut"; un="sa"; pass = "Passw0rd";
        } else if (warehouse.equals("Monheim")) {
            ip= pubVars.GetIp(); port=pubVars.GetPort(); dbName="MONHEIMAPP"; un="sa"; pass = "Passw0rd";
        } else {
            ip= pubVars.GetIp(); port=pubVars.GetPort(); dbName="MLDIAPP"; un="sa"; pass = "Passw0rd";
        }
        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        String ConURL = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConURL="jdbc:jtds:sqlserver://"+ip+":"+port+";"+"databasename="+dbName+";user="+un+";password="+pass+";";
            con= DriverManager.getConnection(ConURL);
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return con;
    }

    public void Reconnect() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
            con = SQLConnection();
            //System.out.println("RECONNECT");
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }
}
