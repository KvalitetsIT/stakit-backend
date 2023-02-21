package dk.kvalitetsit.stakit.integrationtest;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.Subscribe;

import java.util.UUID;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionIT extends AbstractIntegrationTest {
    private final StaKitApi staKitApi;

    public SubscriptionIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        staKitApi = new StaKitApi(apiClient);
    }

    @Test
    public void testSubScribeAndConfirm() throws ApiException {
        int currentMailCount = getCurrentMailCount();
        var subscribeInput = new Subscribe();
        subscribeInput.setEmail("email");
        subscribeInput.setAnnouncements(true);

        var subscribeResponse = staKitApi.v1SubscribePost(subscribeInput);
        assertNotNull(subscribeResponse);
        assertNotNull(subscribeResponse.getUuid());

        RestAssured.baseURI = "http://" + getSmtpHost();
        RestAssured.port = getSmtpWebPort();
        RestAssured.basePath = "/api/v2";

        ValidatableResponse validatableResponse = given()
                .when()
                .get("/messages")
                .then()
                .body("total", equalTo(currentMailCount+1));

        var body = validatableResponse.extract().body().asString();
        Pattern pattern = Pattern.compile(".*http:\\/\\/.+:\\d+\\/v1\\/subscribe\\/(.*?)\".*", Pattern.DOTALL);
        var matcher = pattern.matcher(body);
        assertTrue(body, matcher.matches());
        var uuid = matcher.group(1);

        var confirmResult = staKitApi.v1SubscribeConfirmUuidGetWithHttpInfo(UUID.fromString(uuid));
        assertEquals(201, confirmResult.getStatusCode());
    }
}
