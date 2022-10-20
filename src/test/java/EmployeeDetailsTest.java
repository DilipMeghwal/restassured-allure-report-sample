import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@Epic("REST API Regression Testing using JUnit4")
@Feature("Verify CRUD Operations on Employee module")
public class EmployeeDetailsTest {

    String BaseURL = "https://reqres.in/api";

    @Test
    @Story("GET Request")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description : Verify the details of employee of id-2")
    public void verifyUser() {

        // Given
        given()
                .filter(new AllureRestAssured())

                // WHEN
                .when()
                .get(BaseURL + "/users?page=2")

                // THEN
                .then()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                // To verify booking id at index 2
                .body("total_pages", equalTo(2));
    }

    @Test
    @Story("POST Request")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description : Verify the creation of a new employee")
    public void createUser() {

        JSONObject data = new JSONObject();

        // Map<String, String> map = new HashMap<String, String>();

        data.put("name", "morpheus");
        data.put("job", "leader");

        // GIVEN
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(data.toString())

                // WHEN
                .when()
                .post(BaseURL + "/users")

                // THEN
                .then()
                .statusCode(201)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("leader"));

    }

}