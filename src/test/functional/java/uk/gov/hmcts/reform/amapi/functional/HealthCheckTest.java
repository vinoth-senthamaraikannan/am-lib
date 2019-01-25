package uk.gov.hmcts.reform.amapi.functional;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.restassured.RestAssured.get;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTest extends RestAssuredTest {

    @Test
    @Category(SmokeTest.class)
    public void healthcheck_returns_200() {
        get("/health")
            .then().statusCode(200)
            .and().body("status", equalTo("UP"));
        assertThat(2).isGreaterThan(1);
    }
}
