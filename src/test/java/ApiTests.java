import book.Account;
import book.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

public class ApiTests {
    private static Account account;

    @Test
    public void createUser() throws JsonProcessingException {
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
    public void createBookForUser() throws JsonProcessingException{
        account = new Account("Anna", "password");
        ObjectMapper mapper = new ObjectMapper();
        String url = "http://localhost:8090/Anna/books";
        Book book = new Book(account, "Джон Кехо", "Подсознание может всё", "Какое-то описание");

        RequestSpecification requestSpecification = given().
                contentType(ContentType.JSON).
                body(mapper.writeValueAsBytes(book));

        Response response = requestSpecification.post(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_CREATED)
                .log();
    }

    @Test
    public void changeUserName() throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String url = "http://localhost:8090/user/changeUser/Anna";
        Account account = new Account("HIOTUS", "password");

        RequestSpecification requestSpecification = given().
                contentType(ContentType.JSON).
                body(mapper.writeValueAsBytes(account));
        Response response = requestSpecification.put(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_OK)
                .log();
    }

    @Test
    public void removeBookForUser() {
        String url = "http://localhost:8090/Anna/books/removeBook/" + 18;
        RequestSpecification requestSpecification = given().contentType(ContentType.JSON);
        Response response = requestSpecification.delete(url);
        response.prettyPrint();
        response.then()
                .statusCode(SC_OK)
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
}
