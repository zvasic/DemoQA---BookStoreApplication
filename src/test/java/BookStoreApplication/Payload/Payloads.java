package BookStoreApplication.Payload;

public class Payloads {

    public static String username = "BookReader35";
    private static String password = "BookReader20#";

    public static String userPayload() {
        return "{\n" +
                "    \"userName\" : \"" + username + "\",\n" +
                "    \"password\" : \"" + password + "\"" +
                "}";
    }

    public static String addSingleBookPayload(String userid, String isbn) {
        return "{\n" +
                "\"userId\" : \"" + userid + "\",\n" +
                "\"collectionOfIsbns\" : [\n" +
                "{\n" +
                "\"isbn\" : \"" + isbn + "\"\n" +
                "}\n]\n}";
    }
    public static String addMultipleBookPayload(String userid, String isbn1, String isbn2) {
        return "{\n" +
                "\"userId\" : \"" + userid + "\",\n" +
                "\"collectionOfIsbns\" : [\n" +
                "{\n" +
                "\"isbn\" : \"" + isbn1 + "\"\n" +
                "}\n," +
                "{\n" +
                "\"isbn\" : \"" + isbn2 + "\"\n" +
                "}\n]\n}";
    }
    public static String deleteBookPayload(String isbn, String userid) {
        return "{\n" +
                "    \"isbn\" : \"" + isbn + "\",\n" +
                "    \"userId\" : \"" + userid + "\"" +
                "}";
    }

    public static String updateBookPayload(String userid, String isbn) {
        return "{\n" +
                "    \"userId\" : \"" + userid + "\",\n" +
                "    \"isbn\" : \"" + isbn + "\"" +
                "}";
    }
}