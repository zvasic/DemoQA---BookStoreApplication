package BookStoreApplication;

import BookStoreApplication.Payload.Payloads;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static BookStoreApplication.Payload.Payloads.*;
import static io.restassured.RestAssured.given;

public class BookStoreAPIE2E {

    @Test
    public void BookStoreE2E() {
        RestAssured.baseURI = "https://demoqa.com";
        //String userID = "0b6a4101-6172-4ce0-80d2-7ee452d78d4d";

        // Create Account
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
        String userID = js.getString("userID");
        String userName = js.getString("username");

        Assert.assertEquals(userName, Payloads.username);

        // Generate token

        String responseToken =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .body(userPayload())
                        .post("/Account/v1/GenerateToken")
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(responseToken);
        String token = js.getString("token");

        Assert.assertFalse(js.getString("expires").isEmpty());
        Assert.assertEquals(js.getString("result"), "User authorized successfully.");

        // User Authorized

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

        // Get All Books

        String listOfBooks =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .get("/BookStore/v1/Books")
                        .then()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(listOfBooks);
        String ISBN1 = js.getString("books[0].isbn");
        String ISBN2 = js.getString("books[1].isbn");

        // Get Book Info

        String bookInfo =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .get("/BookStore/v1/Book/?ISBN=" + ISBN1)
                        .then()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(bookInfo);
        String infoBookISBN = js.getString("isbn");

        Assert.assertEquals(ISBN1, infoBookISBN);

        // Get User Before Adding Book
        String userData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + userID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(userData);
        String getUsername = js.getString("username");
        List<String> getBooks = js.getList("books");

        Assert.assertTrue(getBooks.isEmpty());
        Assert.assertEquals(getUsername, username);

        // Add Single Book
        String addBookData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(addSingleBookPayload(userID, ISBN1))
                        .post("/BookStore/v1/Books")
                        .then().log().all()
                        .assertThat().statusCode(201)
                        .extract().response().asString();

        js = new JsonPath(addBookData);
        String addedISBN = js.getString("books[0].isbn");

        Assert.assertEquals(addedISBN, ISBN1);

        // Get User After Adding Book
        String userDataAfterAddedBook =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + userID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(userDataAfterAddedBook);
        String getUsername1 = js.getString("username");
        List<String> getBooks1 = js.getList("books");

        Assert.assertFalse(getBooks1.isEmpty());
        Assert.assertEquals(getUsername1, username);

        // Remove book
        given().log().all()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(deleteBookPayload(ISBN1, userID))
                .delete("/BookStore/v1/Book")
                .then().log().all()
                .assertThat().statusCode(204);

        // Get User After Removing Book
        String userDataAfterRemovedBook =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + userID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(userDataAfterRemovedBook);
        String getUsername2 = js.getString("username");
        List<String> getBooks2 = js.getList("books");

        Assert.assertTrue(getBooks2.isEmpty());
        Assert.assertEquals(getUsername2, username);

        // Add multiple Books
        String addBooksData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(addMultipleBookPayload(userID, ISBN1, ISBN2))
                        .post("/BookStore/v1/Books")
                        .then().log().all()
                        .assertThat().statusCode(201)
                        .extract().response().asString();

        js = new JsonPath(addBooksData);
        String addedISBN1 = js.getString("books[0].isbn");
        String addedISBN2 = js.getString("books[1].isbn");

        Assert.assertEquals(addedISBN1, ISBN1);
        Assert.assertEquals(addedISBN2, ISBN2);

        // Get User After adding Books
        String userDataAfterAddingBooks =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + userID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(userDataAfterAddingBooks);
        String getUsername3 = js.getString("username");
        List<String> getBooks3 = js.getList("books");

        Assert.assertFalse(getBooks3.isEmpty());
        Assert.assertEquals(getUsername3, username);

        // Remove all books
        given()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .delete("/BookStore/v1/Books?UserId=" + userID)
                .then()
                .assertThat().statusCode(204);

        // Add Single Book For Update
        String addNewBookData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(addSingleBookPayload(userID, ISBN1))
                        .post("/BookStore/v1/Books")
                        .then().log().all()
                        .assertThat().statusCode(201)
                        .extract().response().asString();

        js = new JsonPath(addBookData);
        String newAddedISBN = js.getString("books[0].isbn");

        Assert.assertEquals(addedISBN, ISBN1);

        // Update Single Book
        String updateNewBookData =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(updateBookPayload(userID, ISBN2))
                        .put("/BookStore/v1/Books/" + ISBN1)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(updateNewBookData);
        String updatedISBN = js.getString("books[0].isbn");

        Assert.assertEquals(updatedISBN, ISBN2);

        // Get User After Updating Book
        String userDataAfterUpdatingBook =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + userID)
                        .then().log().all()
                        .assertThat().statusCode(200)
                        .extract().response().asString();

        js = new JsonPath(userDataAfterUpdatingBook);
        String getUsername4 = js.getString("username");
        List<String> getBooks4 = js.getList("books");

        Assert.assertFalse(getBooks4.isEmpty());
        Assert.assertEquals(getUsername4, username);

        // Delete User
        given().log().all()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .delete("/Account/v1/User/" + userID)
                .then().log().all()
                .assertThat().statusCode(204);

        // Get User After Deleting USer
        String userDataAfterDelete =
                given().log().all()
                        .when()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .get("/Account/v1/User/" + userID)
                        .then().log().all()
                        .assertThat().statusCode(401)
                        .extract().response().asString();

        js = new JsonPath(userDataAfterDelete);
        String getmessage = js.getString("message");

        Assert.assertEquals(getmessage, "User not found!");
    }
}
