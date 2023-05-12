package dk.kvalitetsit.stakit.integrationtest;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.openapitools.client.api.AnnouncementManagementApi;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.AnnouncementCreate;
import org.openapitools.client.model.AnnouncementUpdate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementIT extends AbstractIntegrationTest {
    private final AnnouncementManagementApi announcementsApi;
    private UUID uuid;
    private AnnouncementCreate announcement;

    public AnnouncementIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        announcementsApi = new AnnouncementManagementApi(apiClient);
    }

    @Before
    public void setup() throws ApiException {
        announcement = new AnnouncementCreate()
                .fromDatetime(OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .toDatetime(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .subject(UUID.randomUUID().toString())
                .message("message");

        uuid = announcementsApi.v1AnnouncementsPost(announcement).getUuid();
    }

    @Test
    public void testGet() throws ApiException {
        var result = announcementsApi.v1AnnouncementsUuidGet(uuid);
        assertNotNull(result);

        assertEquals(announcement.getFromDatetime().toInstant(), result.getFromDatetime().toInstant());
        assertEquals(announcement.getToDatetime().toInstant(), result.getToDatetime().toInstant());
        assertEquals(announcement.getSubject(), result.getSubject());
        assertEquals(announcement.getMessage(), result.getMessage());
        assertEquals(uuid, result.getUuid());
    }

    @Test
    public void testDelete() throws ApiException {
        announcementsApi.v1AnnouncementsUuidDelete(uuid);

        var expectedException = assertThrows(ApiException.class, () -> announcementsApi.v1AnnouncementsUuidGet(uuid));
        assertEquals(404, expectedException.getCode());
    }

    @Test
    public void testCreateAndUpdate() throws ApiException {
        // Create is done in setup method

        var input = new AnnouncementUpdate()
                .fromDatetime(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .subject("upadated subject")
                .message("updated message")
                .toDatetime(null);

        announcementsApi.v1AnnouncementsUuidPut(uuid, input);

        // Read to check ressource have been updated.
        var result = announcementsApi.v1AnnouncementsUuidGet(uuid);
        assertNotNull(result);

        assertEquals(input.getFromDatetime().toInstant(), result.getFromDatetime().toInstant());
        assertNull(result.getToDatetime());
        assertEquals(input.getSubject(), result.getSubject());
        assertEquals(input.getMessage(), result.getMessage());
        assertEquals(uuid, result.getUuid());
    }


    @Test
    public void testAnnouncementPostSendsMailWhenSubscribed() throws ApiException, SQLException {
        int mailCount = getCurrentMailCount();
        var postAnnouncement = new AnnouncementCreate()
                .fromDatetime(OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .toDatetime(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .subject(UUID.randomUUID().toString())
                .message("<b>This is the announcement</b>");

        executeSql(("insert into mail_subscription(uuid, " +
                "                                 announcements, " +
                "                                 confirmed, " +
                "                                 confirm_identifier, " +
                "                                 email) " +
                "                   values('%s', " +
                "                          1, " +
                "                          1, " +
                "                          '%s', " +
                "                          '%s')")
                .formatted(UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        "hello_email"));

        var result = announcementsApi.v1AnnouncementsPostWithHttpInfo(postAnnouncement);
        assertNotNull(result);
        assertEquals(201, result.getStatusCode());

        RestAssured.baseURI = "http://" + getSmtpHost();
        RestAssured.port = getSmtpWebPort();
        RestAssured.basePath = "/api/v2";

        ValidatableResponse validatableResponse = given()
                .when()
                .get("/messages")
                .then()
                .body("total", equalTo(mailCount+1));

        var body = validatableResponse.extract().body().asString();
        Pattern pattern = Pattern.compile(".*Content-Type: text\\/html.*u003c.*b.*u003e.*This is the announcement.*u003c.*\\/b.*u003e.*http:\\/\\/.+:\\d+\\/unsubscribe\\/(.*?).*", Pattern.DOTALL);
        var matcher = pattern.matcher(body);
        assertTrue(body, matcher.matches());
    }

    private void executeSql(String sql) throws SQLException {
        try(var connection = getConnection()) {
            connection.createStatement().executeUpdate(sql);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl(), ServiceStarter.DB_USER, ServiceStarter.DB_PASSWORD);
    }

}
