package dk.kvalitetsit.stakit.integrationtest;

import io.restassured.RestAssured;
import org.junit.Ignore;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.AdapterApi;
import org.openapitools.client.model.StatusUpdate;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.equalTo;

public class AdapterIT extends AbstractIntegrationTest {

    private final AdapterApi adapterApi;

    public AdapterIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("X-API-KEY", ServiceStarter.API_KEY);

        adapterApi = new AdapterApi(apiClient);
    }

    @Test
    public void testCallService() throws ApiException {
        var currentMailCount = getCurrentMailCount();
        var input = new StatusUpdate();

        input.setService("service-id");
        input.setServiceName("This is a service name");
        input.setStatus(StatusUpdate.StatusEnum.OK);
        input.setStatusTime(OffsetDateTime.now());
        input.setMessage("Everything is OK.");

        var result = adapterApi.v1StatusPostWithHttpInfo(input);
        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode());

        RestAssured.baseURI = "http://" + getSmtpHost();
        RestAssured.port = getSmtpWebPort();
        RestAssured.basePath = "/api/v2";

        given()
                .when()
                    .get("/messages")
                .then()
                    .body("total", equalTo(currentMailCount + 0));
    }

    @Test
    @Ignore("Implement when subscribe API is implemented")
    public void testCallUpdateMailSendOnStatusChange() throws ApiException {
        var input = new StatusUpdate();

        input.setService("service-id");
        input.setServiceName("This is a service name");
        input.setStatus(StatusUpdate.StatusEnum.OK);
        input.setStatusTime(OffsetDateTime.now());
        input.setMessage("Everything is OK.");

        var result = adapterApi.v1StatusPostWithHttpInfo(input);
        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode());
    }
}
