package MssqlCon;

import com.google.android.material.navigation.NavigationView;

public class PublicVars {
    private static String _user;
    private static NavigationView _mainNav;

    public static void SetUser(String User) {
        _user = User;
    }
    public static String GetUser(){
        return _user;
    }

    public static void SetMainNav(NavigationView Nav) { _mainNav = Nav;}
    public static NavigationView GetNav() {return _mainNav;}

    private static String _ip;
    public static void SetIp(String Ip) { _ip = Ip; }
    public static String GetIp() { return _ip; }

    private static String _warehouse;
    public static void SetWarehouse(String warehouse) { _warehouse = warehouse; }
    public static String GetWarehouse() {return  _warehouse; }

    public static String _remarks;
    public static void SetInvtRemarks(String remarks) { _remarks = remarks;}
    public static String GetInvtRemarks(){return  _remarks;}

    public static boolean _mode;
    public static void SetOfflineMode(boolean offlineMode) { _mode = offlineMode;}
    public static Boolean GetOfflineMode() {return  _mode; }

    public static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "barcodesys.db";
}
