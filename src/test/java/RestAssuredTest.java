import book.Account;
import book.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;


public class RestAssuredTest {
    private static Account account = new Account("Anna", "password");

    private static int counter = 2;

    @Test
    public void postUser() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String url = "http://localhost:8090/user/addUser";
        account = new Account("Anna", "password");
        RequestSpecification requestSpecification = given().
                contentType(ContentType.JSON).
                body(mapper.writeValueAsBytes(account));
        Response response = requestSpecification.post(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_CREATED)
                .log();

    }

    @Test
    public void removeUser() {
        String url = "http://localhost:8090/user/removeUser/Anna";
        RequestSpecification requestSpecification = given().contentType(ContentType.JSON);
        Response response = requestSpecification.delete(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_OK)
                .log();
    }

    @Test
    public void postAndRemoveBook() throws JsonProcessingException, JSONException {
     /*   Account account = new Account("Anna", "password");
        ObjectMapper mapper = new ObjectMapper();
        String url = "http://localhost:8090/Anna/books";
        Book book = new Book(account, "Anna Karenina", "Leo Tolstoy", "description");

        RequestSpecification requestSpecification = given().
                contentType(ContentType.JSON).
                body(mapper.writeValueAsBytes(book));

        Response response = requestSpecification.post(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_CREATED)
                .log();*/
        String url = "http://localhost:8090/Anna/books/removeBook/" + 19;
        //counter = counter + 2;
        RequestSpecification requestSpecification = given().contentType(ContentType.JSON);
        Response response = requestSpecification.delete(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_OK)
                .log();
    }

    private String getLastId() throws JSONException {
        String url = "http://localhost:8090/user/allUsers";
        RequestSpecification requestSpecification = given().contentType(ContentType.JSON);
        Response response = requestSpecification.get(url);
        response.prettyPrint();
        JSONArray jsonResponse = new JSONArray(response.asString());
        return jsonResponse.getJSONObject(0).getString("id");
    }

}
