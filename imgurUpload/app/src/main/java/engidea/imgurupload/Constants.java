package engidea.imgurupload;

public class Constants {
    /*
      Logging flag
     */
    public static final boolean LOGGING = true;

    /*
      Your imgur client id. You need this to upload to imgur.

      More here: https://api.imgur.com/
     */
    private static final String MY_IMGUR_CLIENT_ID = "c5ef4ca03ff3300";
//    public static final String MY_IMGUR_CLIENT_SECRET = "ed65b56a218c842c126244e1c90125bd864696e1";

    /*
      Client Auth
     */
    public static String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

}
