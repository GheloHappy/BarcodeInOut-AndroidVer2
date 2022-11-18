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
}
