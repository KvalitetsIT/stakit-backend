package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class StatusUpdateServiceImplTest {
    private StatusUpdateService statusUpdateService;
    private ServiceConfigurationDao serviceConfigurationDao;
    private ServiceStatusDao serviceStatusDao;
    private MailService mailService;

    @Before
    public void setup() {
        serviceConfigurationDao = Mockito.mock(ServiceConfigurationDao.class);
        serviceStatusDao = Mockito.mock(ServiceStatusDao.class);
        mailService = Mockito.mock(MailService.class);
        statusUpdateService = new StatusUpdateServiceImpl(serviceConfigurationDao, serviceStatusDao, mailService);
    }

    @Test
    public void testStatusConfigurationNotFound() {
        var input = new UpdateServiceInput(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Status.NOT_OK, OffsetDateTime.now(), "Some message");

        Mockito.when(serviceConfigurationDao.insert(Mockito.any())).thenReturn(10L);
        Mockito.when(serviceStatusDao.findLatest(Mockito.any())).thenReturn(Optional.empty());
        statusUpdateService.updateStatus(input);

        Mockito.verify(serviceConfigurationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(input.service(), x.service());
            assertEquals(input.serviceName(), x.name());
            assertFalse(x.ignoreServiceName());
            assertNull(x.groupConfigurationId());
            assertNotNull(x.uuid());

            return true;
        }));
        Mockito.verify(serviceStatusDao, times(1)).insert(ServiceStatusEntity.createInstance(10L, "NOT_OK", input.statusDateTime(), input.message()));
        Mockito.verify(serviceStatusDao, times(1)).findLatest(input.service());

        Mockito.verifyNoMoreInteractions(serviceConfigurationDao, serviceStatusDao, mailService);
    }

    @Test
    public void testStatusConfigurationFoundNoStatusChange() {
        var input = new UpdateServiceInput(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Status.NOT_OK, OffsetDateTime.now(), "Some message");
        var serviceStatus = new ServiceStatusEntity(10L, 10L, "NOT_OK", OffsetDateTime.now(), "message");

        Mockito.when(serviceConfigurationDao.insert(Mockito.any())).thenThrow(DuplicateKeyException.class);
        Mockito.when(serviceConfigurationDao.findByService(input.service())).thenReturn(new ServiceConfigurationEntity(10L, UUID.randomUUID(), "service-name", "service", true, null));
        Mockito.when(serviceStatusDao.findLatest(Mockito.any())).thenReturn(Optional.of(serviceStatus));
        statusUpdateService.updateStatus(input);

        Mockito.verify(serviceConfigurationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(input.service(), x.service());
            assertEquals(input.serviceName(), x.name());
            assertFalse(x.ignoreServiceName());
            assertNull(x.groupConfigurationId());
            assertNotNull(x.uuid());

            return true;
        }));

        Mockito.verify(serviceConfigurationDao, times(1)).findByService(input.service());
        Mockito.verify(serviceStatusDao, times(1)).insert(ServiceStatusEntity.createInstance(10L, "NOT_OK", input.statusDateTime(), input.message()));
        Mockito.verify(serviceStatusDao, times(1)).findLatest(input.service());

        Mockito.verifyNoMoreInteractions(serviceConfigurationDao, serviceStatusDao, mailService);
    }

    @Test
    public void testStatusConfigurationFoundStatusChange() {
        var input = new UpdateServiceInput(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Status.OK, OffsetDateTime.now(), "Some message");
        var serviceStatus = new ServiceStatusEntity(10L, 10L, "NOT_OK", OffsetDateTime.now(), "message");

        Mockito.when(serviceConfigurationDao.insert(Mockito.any())).thenThrow(DuplicateKeyException.class);
        Mockito.when(serviceConfigurationDao.findByService(input.service())).thenReturn(new ServiceConfigurationEntity(10L, UUID.randomUUID(), "service-name", "service", true, null));
        Mockito.when(serviceStatusDao.findLatest(Mockito.any())).thenReturn(Optional.of(serviceStatus));
        Mockito.when(serviceStatusDao.insert(Mockito.any())).thenReturn(11L);

        statusUpdateService.updateStatus(input);

        Mockito.verify(serviceConfigurationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(input.service(), x.service());
            assertEquals(input.serviceName(), x.name());
            assertFalse(x.ignoreServiceName());
            assertNull(x.groupConfigurationId());
            assertNotNull(x.uuid());

            return true;
        }));

        Mockito.verify(serviceConfigurationDao, times(1)).findByService(input.service());
        Mockito.verify(serviceStatusDao, times(1)).insert(ServiceStatusEntity.createInstance(10L, "OK", input.statusDateTime(), input.message()));
        Mockito.verify(serviceStatusDao, times(1)).findLatest(input.service());
        Mockito.verify(mailService, times(1)).queueStatusUpdatedMail(10L, 11L);

        Mockito.verifyNoMoreInteractions(serviceConfigurationDao, serviceStatusDao, mailService);
    }
}
