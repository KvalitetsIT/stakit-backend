package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.AdapterApi;
import org.openapitools.client.api.AnnouncementsApi;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.AnnouncementCreate;
import org.openapitools.client.model.ServiceStatus;
import org.openapitools.client.model.StatusUpdate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StaKitIT extends AbstractIntegrationTest {

    private final StaKitApi staKitApi;
    private final AdapterApi adapterApi;
    private final AnnouncementsApi announcementApi;

    public StaKitIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
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
    public void testCallGetGroupedStatus() throws ApiException {
        var statusUpdate = new StatusUpdate();
        statusUpdate.setService(UUID.randomUUID().toString());
        statusUpdate.setServiceName("Service Name Test Status Get");
        statusUpdate.setStatus(StatusUpdate.StatusEnum.NOT_OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());

        adapterApi.v1StatusPost(statusUpdate);

        statusUpdate.status(StatusUpdate.StatusEnum.OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());

        var result = staKitApi.v1ServiceStatusGroupedGet();

        assertNotNull(result);
        assertNotNull(result.getStatusGroup());
        assertEquals(1, result.getStatusGroup().size());

        var group = result.getStatusGroup().get(0);
        assertEquals("Default", group.getGroupName());

        assertNotNull(group.getServices());
        assertTrue(group.getServices().size() >= 1);

        assertEquals(1L, group.getServices().stream().filter(x -> x.getServiceName().equals(statusUpdate.getServiceName())).count());
        assertEquals(ServiceStatus.StatusEnum.NOT_OK, group.getServices().stream().filter(x -> x.getServiceName().equals(statusUpdate.getServiceName())).findAny().get().getStatus());
    }

    @Test
    public void testCallGetAnnouncementsToShow() throws ApiException {
        var announcementOne = new AnnouncementCreate()
                .fromDatetime(OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .toDatetime(OffsetDateTime.now().minusMinutes(10).truncatedTo(ChronoUnit.SECONDS))
                .subject(UUID.randomUUID().toString())
                .message("message");

        var announcementTwo = new AnnouncementCreate()
                .fromDatetime(OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .toDatetime(OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .subject(UUID.randomUUID().toString())
                .message("message");

        announcementApi.v1AnnouncementsPost(announcementOne);
        announcementApi.v1AnnouncementsPost(announcementTwo);

        var result = staKitApi.v1AnnouncementsToShowGet();
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(announcementTwo.getFromDatetime(), result.get(0).getFromDatetime());
        assertEquals(announcementTwo.getToDatetime(), result.get(0).getToDatetime());
        assertEquals(announcementTwo.getSubject(), result.get(0).getSubject());
        assertEquals(announcementTwo.getMessage(), result.get(0).getMessage());
    }
}
