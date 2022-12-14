package dk.kvalitetsit.stakit.integrationtest;

import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.AdapterApi;
import org.openapitools.client.api.AnnouncementsApi;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.Subscribe;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionIT extends AbstractIntegrationTest {
    private final AdapterApi adapterApi;
    private final StaKitApi staKitApi;
    private final AnnouncementsApi announcementApi;

    public SubscriptionIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        var authenticatedApi = new ApiClient();
        authenticatedApi.setBasePath(getApiBasePath());
        authenticatedApi.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        staKitApi = new StaKitApi(apiClient);
        adapterApi = new AdapterApi(apiClient);
        announcementApi = new AnnouncementsApi(authenticatedApi);
    }

    @Test
    public void testSubScribeAndConfirm() throws ApiException {
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
                .body("total", equalTo(1));

        var body = validatableResponse.extract().body().asString();
        Pattern pattern = Pattern.compile(".*https:\\/\\/some\\/url\\/(.*?)\".*", Pattern.DOTALL);
        var matcher = pattern.matcher(body);
        assertTrue(matcher.matches());
        var uuid = matcher.group(1);

        var confirmResult = staKitApi.v1SubscribeConfirmUuidGetWithHttpInfo(UUID.fromString(uuid));
        assertEquals(201, confirmResult.getStatusCode());
    }
}
