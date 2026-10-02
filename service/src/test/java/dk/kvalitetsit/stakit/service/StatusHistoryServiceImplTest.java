package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StatusHistoryServiceImplTest {
    private StatusHistoryServiceImpl statusHistoryService;
    private ServiceConfigurationDao serviceConfigurationDao;
    private ServiceStatusDao serviceStatusDao;
    private UUID serviceUuid;

    @BeforeEach
    public void setup() {
        serviceConfigurationDao = Mockito.mock(ServiceConfigurationDao.class);
        serviceStatusDao = Mockito.mock(ServiceStatusDao.class);

        statusHistoryService = new StatusHistoryServiceImpl(serviceConfigurationDao, serviceStatusDao);

        serviceUuid = UUID.randomUUID();
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid))
                .thenReturn(Optional.of(new ServiceConfigurationEntityWithGroupUuid(1L, serviceUuid, "service", "name", false, UUID.randomUUID(), "OK", null)));
    }

    @Test
    public void testServiceNotFound() {
        var unknown = UUID.randomUUID();
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(unknown)).thenReturn(Optional.empty());

        var result = statusHistoryService.getStatusHistory(unknown, OffsetDateTime.now().minusDays(1));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFromTooOld() {
        assertThrows(InvalidDataException.class, () -> statusHistoryService.getStatusHistory(serviceUuid, OffsetDateTime.now().minusDays(120)));
    }

    @Test
    public void testNinetyDaysAllowed() {
        var from = OffsetDateTime.now().minusDays(90);
        Mockito.when(serviceStatusDao.findLatestBefore(serviceUuid, from)).thenReturn(Optional.empty());
        Mockito.when(serviceStatusDao.findFrom(serviceUuid, from)).thenReturn(Collections.emptyList());

        var result = statusHistoryService.getStatusHistory(serviceUuid, from);

        assertTrue(result.isPresent());
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void testNoStatusChangeInPeriod() {
        var from = OffsetDateTime.now().minusDays(7);
        Mockito.when(serviceStatusDao.findLatestBefore(serviceUuid, from))
                .thenReturn(Optional.of(status("NOT_OK", from.minusDays(3), "Down")));
        Mockito.when(serviceStatusDao.findFrom(serviceUuid, from)).thenReturn(Collections.emptyList());

        var result = statusHistoryService.getStatusHistory(serviceUuid, from).orElseThrow();

        assertEquals(1, result.size());
        assertEquals(Status.NOT_OK, result.get(0).status());
        assertEquals(from, result.get(0).from());
        assertNull(result.get(0).to());
        assertEquals("Down", result.get(0).message());
    }

    @Test
    public void testRepeatedStatusIsMerged() {
        var from = OffsetDateTime.now().minusDays(7);
        var t1 = from.plusHours(1);
        var t2 = from.plusHours(2);
        var t3 = from.plusHours(3);
        var t4 = from.plusHours(4);
        var t5 = from.plusHours(5);

        Mockito.when(serviceStatusDao.findLatestBefore(serviceUuid, from))
                .thenReturn(Optional.of(status("OK", from.minusDays(1), null)));
        Mockito.when(serviceStatusDao.findFrom(serviceUuid, from)).thenReturn(List.of(
                status("OK", t1, null),
                status("NOT_OK", t2, "Database down"),
                status("NOT_OK", t3, "Database still down"),
                status("PARTIAL_NOT_OK", t4, "Recovering"),
                status("OK", t5, null)));

        var result = statusHistoryService.getStatusHistory(serviceUuid, from).orElseThrow();

        assertEquals(4, result.size());

        assertEquals(Status.OK, result.get(0).status());
        assertEquals(from, result.get(0).from());
        assertEquals(t2, result.get(0).to());

        assertEquals(Status.NOT_OK, result.get(1).status());
        assertEquals(t2, result.get(1).from());
        assertEquals(t4, result.get(1).to());
        assertEquals("Database down", result.get(1).message());

        assertEquals(Status.PARTIAL_NOT_OK, result.get(2).status());
        assertEquals(t4, result.get(2).from());
        assertEquals(t5, result.get(2).to());

        assertEquals(Status.OK, result.get(3).status());
        assertEquals(t5, result.get(3).from());
        assertNull(result.get(3).to());
    }

    @Test
    public void testNoStatusBeforePeriod() {
        var from = OffsetDateTime.now().minusDays(7);
        var t1 = from.plusDays(2);

        Mockito.when(serviceStatusDao.findLatestBefore(serviceUuid, from)).thenReturn(Optional.empty());
        Mockito.when(serviceStatusDao.findFrom(serviceUuid, from)).thenReturn(List.of(status("NOT_OK", t1, "Down")));

        var result = statusHistoryService.getStatusHistory(serviceUuid, from).orElseThrow();

        assertEquals(1, result.size());
        assertEquals(Status.NOT_OK, result.get(0).status());
        assertEquals(t1, result.get(0).from());
        assertNull(result.get(0).to());
    }

    private static ServiceStatusEntity status(String status, OffsetDateTime time, String message) {
        return new ServiceStatusEntity(null, 1L, status, time, message);
    }
}
