package MssqlCon;

import android.content.Context;
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

    private Context context;  // Add this variable

    public Login(Context context) {
        this.context = context;
    }

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

                    User user = new User(rs.getInt(1), rs.getString(2),
                            rs.getString(3), rs.getString(4), rs.getString(5));
                    UserDbHelper userDbHelper = new UserDbHelper(context);
                    userDbHelper.insertUser(user);

                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
