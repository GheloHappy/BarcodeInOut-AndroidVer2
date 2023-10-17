package MssqlCon;

import android.util.Log;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import LocalDb.User;
import LocalDb.UserDbHelper;

public class Login extends  SqlCon {
    PublicVars pubVar = new PublicVars();

    public boolean CheckUser(String usr, String pass ) {
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

    public  List<Map<String, String>> GetUserDetails(String user) {
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_Users WHERE username ='" + user + "'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while(rs.next()) {
                    Map<String, String> dtTempBarTran = new HashMap<>();
                    dtTempBarTran.put("id", String.valueOf(rs.getInt("id")));
                    dtTempBarTran.put("username", rs.getString("username"));
                    dtTempBarTran.put("password", rs.getString("password"));
                    dtTempBarTran.put("name", rs.getString("name"));
                    dtTempBarTran.put("department", rs.getString("department"));
                    data.add(dtTempBarTran);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return  data;
    }
}
