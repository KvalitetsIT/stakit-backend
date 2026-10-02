package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.StatusPeriodModel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatusHistoryService {
    Optional<List<StatusPeriodModel>> getStatusHistory(UUID serviceUuid, OffsetDateTime from);
}
