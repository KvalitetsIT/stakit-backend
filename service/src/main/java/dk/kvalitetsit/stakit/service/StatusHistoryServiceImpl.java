package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusPeriodModel;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StatusHistoryServiceImpl implements StatusHistoryService {
    static final Duration MAX_HISTORY = Duration.ofDays(90);

    private final ServiceConfigurationDao serviceConfigurationDao;
    private final ServiceStatusDao serviceStatusDao;

    public StatusHistoryServiceImpl(ServiceConfigurationDao serviceConfigurationDao, ServiceStatusDao serviceStatusDao) {
        this.serviceConfigurationDao = serviceConfigurationDao;
        this.serviceStatusDao = serviceStatusDao;
    }

    @Override
    @Transactional
    public Optional<List<StatusPeriodModel>> getStatusHistory(UUID serviceUuid, OffsetDateTime from) {
        if(from.isBefore(OffsetDateTime.now().minus(MAX_HISTORY).minusDays(1))) {
            throw new InvalidDataException("History can not be requested more than %s days back.".formatted(MAX_HISTORY.toDays()));
        }

        if(serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid).isEmpty()) {
            return Optional.empty();
        }

        var periods = new ArrayList<StatusPeriodModel>();

        StatusPeriodModel current = serviceStatusDao.findLatestBefore(serviceUuid, from)
                .map(x -> new StatusPeriodModel(Status.valueOf(x.status()), from, null, x.message()))
                .orElse(null);

        for(ServiceStatusEntity entity : serviceStatusDao.findFrom(serviceUuid, from)) {
            var status = Status.valueOf(entity.status());

            if(current != null && current.status() == status) {
                continue;
            }

            if(current != null) {
                periods.add(new StatusPeriodModel(current.status(), current.from(), entity.statusTime(), current.message()));
            }
            current = new StatusPeriodModel(status, entity.statusTime(), null, entity.message());
        }

        if(current != null) {
            periods.add(current);
        }

        return Optional.of(periods);
    }
}
