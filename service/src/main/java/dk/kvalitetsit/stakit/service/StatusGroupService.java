package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.StatusGrouped;

import java.util.List;

public interface StatusGroupService {
    List<StatusGrouped> getStatusGrouped();
}
