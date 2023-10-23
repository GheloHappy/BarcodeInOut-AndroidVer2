package MssqlCon;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

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
        String ip, port = "1433", dbName ="", un ="sa", pass  = "Passw0rd", warehouse;


        ip = pubVars.GetIp();
        if(ip.equals("192.168.1.249") || ip.equals("192.168.2.249")) {
            pubVars.SetWarehouse("Cabrera");
            dbName="BarcodeInOut";//mdiserver-l Cabrera
        } else if (ip.equals("192.168.1.252") || ip.equals("192.168.2.252")) {
            pubVars.SetWarehouse("Monheim");
            dbName="MONHEIMAPP";
        } else if (ip.equals("192.168.1.248") || ip.equals("192.168.2.248")) {
            pubVars.SetWarehouse("Maryland");
            dbName="MLDIAPP";
        } else {
            ip = "";
            dbName ="";
        }

        if (!ip.equals("")) {
            StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(tp);
            String ConURL = null;

            try{
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                ConURL="jdbc:jtds:sqlserver://"+ip+":"+port+";"+"databasename="+dbName+";user="+un+";password="+pass+";";
                con= DriverManager.getConnection(ConURL);
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            return con = null;
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
