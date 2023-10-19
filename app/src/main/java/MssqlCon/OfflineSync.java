package MssqlCon;

import android.content.Context;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfflineSync extends  SqlCon{

    private Context context;

    public OfflineSync(Context context) {
        this.context = context;
    }

    public List<Map<String, String>> getOfflineProducts() {
        con = SQLConnection();
        List<Map<String, String>> data;
        data = new ArrayList<>();

        try {
            if (con != null) {
                String query = "SELECT * FROM barcodesys_products";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("barcode", rs.getString("barcode"));
                    map.put("description", rs.getString("description"));
                    map.put("solomonID", rs.getString("solomonID"));
                    map.put("uom", rs.getString("uom"));
                    map.put("csPkg", rs.getString("csPkg"));
                    data.add(map);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

}
