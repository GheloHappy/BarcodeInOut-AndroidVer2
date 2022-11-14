package MssqlCon;

public class PublicVars {
    private static String _user;

    public static void SetUser(String User) {
        _user = User;
    }

    public static String GetUser(){
        return _user;
    }
}
