package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.StatusConfigurationDao;
import dk.kvalitetsit.stakit.dao.StatusDao;
import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.times;

public class StatusUpdateServiceImplTest {
    private StatusUpdateService statusUpdateService;
    private StatusConfigurationDao statusConfigurationDao;
    private StatusDao statusDao;

    @Before
    public void setup() {
        statusConfigurationDao = Mockito.mock(StatusConfigurationDao.class);
        statusDao = Mockito.mock(StatusDao.class);
        statusUpdateService = new StatusUpdateServiceImpl(statusConfigurationDao, statusDao);
    }

    @Test
    public void testStatusConfigurationNotFound() {
        var input = new UpdateServiceInput(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Status.NOT_OK, OffsetDateTime.now(), "Some message");

        Mockito.when(statusConfigurationDao.insert(StatusConfigurationEntity.createInstance(input.service(), input.serviceName(), false))).thenReturn(10L);

        statusUpdateService.updateStatus(input);

        Mockito.verify(statusConfigurationDao, times(1)).insert(StatusConfigurationEntity.createInstance(input.service(), input.serviceName(), false));
        Mockito.verify(statusDao, times(1)).insertUpdate(StatusEntity.createInstance(10L, "NOT_OK", input.statusDateTime(), input.message()));

        Mockito.verifyNoMoreInteractions(statusConfigurationDao, statusDao);
    }

    @Test
    public void testStatusConfigurationFound() {
        var input = new UpdateServiceInput(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Status.NOT_OK, OffsetDateTime.now(), "Some message");

        Mockito.when(statusConfigurationDao.insert(StatusConfigurationEntity.createInstance(input.service(), input.serviceName(), false))).thenThrow(DuplicateKeyException.class);
        Mockito.when(statusConfigurationDao.findByService(input.service())).thenReturn(new StatusConfigurationEntity(10L, "service-name", "service", true));

        statusUpdateService.updateStatus(input);

        Mockito.verify(statusConfigurationDao, times(1)).insert(StatusConfigurationEntity.createInstance(input.service(), input.serviceName(), false));
        Mockito.verify(statusConfigurationDao, times(1)).findByService(input.service());
        Mockito.verify(statusDao, times(1)).insertUpdate(StatusEntity.createInstance(10L, "NOT_OK", input.statusDateTime(), input.message()));

        Mockito.verifyNoMoreInteractions(statusConfigurationDao, statusDao);
    }

}
