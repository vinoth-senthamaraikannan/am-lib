package uk.gov.hmcts.reform.amapi.functional;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

public class HelloWorldTest extends RestAssuredTest {

    @Test
    @Category(SmokeTest.class)
    public void helloWorld_returns_200() {
        get("/")
                .then().statusCode(200);
        assertThat(2).isGreaterThan(1);

    }
}
