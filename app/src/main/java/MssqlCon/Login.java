package MssqlCon;

import android.util.Log;

import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends  SqlCon {
    PublicVars pubVar = new PublicVars();

    public boolean CheckUser(String usr, String pass) {
        con = SQLConnection();
        Logs log = new Logs();
        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_Users WHERE username ='" + usr + "' AND password = '" + pass + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    pubVar.SetUser(rs.getString(2));
                    pubVar.SetUserDept(rs.getString(5));
                    log.InsertUserLog("Login", "");
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return false;
    }
}
