package MssqlCon;

import android.util.Log;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Logs extends SqlCon {
    PublicVars pubVar = new PublicVars();

    public void InsertUserLog(String trans, String remarks){
        String user = pubVar.GetUser();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dfTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dfTime.format(c);

        try {
            con = SQLConnection();
            if (con != null) {
                String query = "INSERT INTO logs VALUES('"+user+ "','" + currentDateTime + "','"+ trans +"','"+ remarks + "')";
                Statement st = con.createStatement();
                st.execute(query);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}
