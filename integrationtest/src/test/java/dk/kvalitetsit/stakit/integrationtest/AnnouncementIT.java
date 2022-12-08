package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Before;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.AnnouncementsApi;
import org.openapitools.client.model.AnnouncementCreate;
import org.openapitools.client.model.AnnouncementUpdate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementIT extends AbstractIntegrationTest {
    private final AnnouncementsApi announcementsApi;
    private UUID uuid;
    private AnnouncementCreate group;

    public AnnouncementIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        announcementsApi = new AnnouncementsApi(apiClient);
    }

    @Before
    public void setup() throws ApiException {
        group = new AnnouncementCreate()
                .fromDatetime(OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .toDatetime(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .subject(UUID.randomUUID().toString())
                .message("message");

        uuid = announcementsApi.v1AnnouncementsPost(group).getUuid();
    }

    @Test
    public void testGet() throws ApiException {
        var result = announcementsApi.v1AnnouncementsUuidGet(uuid);
        assertNotNull(result);

        assertEquals(group.getFromDatetime(), result.getFromDatetime());
        assertEquals(group.getToDatetime(), result.getToDatetime());
        assertEquals(group.getSubject(), result.getSubject());
        assertEquals(group.getMessage(), result.getMessage());
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

        assertEquals(input.getFromDatetime(), result.getFromDatetime());
        assertEquals(input.getToDatetime(), result.getToDatetime());
        assertEquals(input.getSubject(), result.getSubject());
        assertEquals(input.getMessage(), result.getMessage());
        assertEquals(uuid, result.getUuid());
    }
}
