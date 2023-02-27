package MssqlCon;

import com.google.android.material.navigation.NavigationView;

public class PublicVars {
    private static String _user;
    private static String _userDept;
    private static NavigationView _mainNav;

    public static void SetUser(String User) {
        _user = User;
    }
    public static String GetUser(){
        return _user;
    }

    public static void SetUserDept(String UserDept) {_userDept = UserDept; }
    public static String GetUserDept(){
        return _userDept;
    }

    public static void SetMainNav(NavigationView Nav) { _mainNav = Nav;}
    public static NavigationView GetNav() {return _mainNav;}

    private static String _ip;
    public static void SetIp(String Ip) { _ip = Ip; }
    public static String GetIp() { return _ip; }

    private static String _port;
    public static void SetPort(String Port) { _port = Port; }
    public static String GetPort() { return _port; }

    private static String _warehouse;

    public static void SetWarehouse(String warehouse) { _warehouse = warehouse; }
    public static String GetWarehouse() {return  _warehouse; }
}
