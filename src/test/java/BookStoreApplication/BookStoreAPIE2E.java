package BookStoreApplication;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import static BookStoreApplication.Payload.Payloads.*;
import static io.restassured.RestAssured.given;

public class BookStoreAPI {
    private String userID;

    @Test
    public void createUser() {
        RestAssured.baseURI = "https://demoqa.com";

        String responseUserID =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .body(userPayload())
                        .post("/Account/v1/User")
                        .then().log().all()
                        .assertThat().statusCode(201)
                        .extract().response().asString();

        JsonPath js = new JsonPath(responseUserID);
        userID = js.getString("userID");
        System.out.println(userID);
    }

    @Test
    public String generateToken() {
        RestAssured.baseURI = "https://demoqa.com";

        String responseToken =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .body(userPayload())
                        .post("/Account/v1/GenerateToken")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js = new JsonPath(responseToken);
        String token = js.getString("token");

        Assert.assertFalse(js.getString("expires").isEmpty());
        Assert.assertEquals(js.getString("result"), "User authorized successfully.");
        return token;
    }

    @Test
    public void userAuthorized() {
        RestAssured.baseURI = "https://demoqa.com";
        String token = generateToken();

        String userAuthorized =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(userPayload())
                        .post("/Account/v1/Authorized")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        Assert.assertEquals(userAuthorized, "true");
    }

    @Test
    public void getUser() {
        RestAssured.baseURI = "https://demoqa.com";

        String useridfortesting = "89b69bad-9cd4-43db-a32a-3794bfc63395";
        String token = generateToken();

        String userData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + useridfortesting)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();
    }

    @Test
    public void getAllBooks() {
        RestAssured.baseURI = "https://demoqa.com";

        String listOfBooks =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .get("/BookStore/v1/Books")
                        .then()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js = new JsonPath(listOfBooks);
        String ISBN1 = js.getString("books[0].isbn");
        String ISBN2 = js.getString("books[1].isbn");
    }

    @Test
    public void getBook() {
        RestAssured.baseURI = "https://demoqa.com";
        String ISBN1 = "9781449331818";
        String bookData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .get("/BookStore/v1/Book/?ISBN=" + ISBN1)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        JsonPath js = new JsonPath(bookData);
        String currentISBN = js.getString("isbn");

        Assert.assertEquals(currentISBN, ISBN1);
    }

    @Test
    public void addBookToList() {
        RestAssured.baseURI = "https://demoqa.com";
        String ISBN1 = "9781449331818";
        String useridfortesting = "89b69bad-9cd4-43db-a32a-3794bfc63395";
        String token = generateToken();

        String addedBookData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(addSingleBookPayload(useridfortesting, ISBN1))
                        .post("/BookStore/v1/Books")
                        .then().log().all()
                        .assertThat().statusCode(201)
                        .extract().response().asString();

        JsonPath js = new JsonPath(addedBookData);
        String currentISBN = js.getString("books[0].isbn");

        Assert.assertEquals(currentISBN, ISBN1);
    }


}
