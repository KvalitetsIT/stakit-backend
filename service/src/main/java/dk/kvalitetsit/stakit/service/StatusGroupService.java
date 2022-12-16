package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.StatusGroupedModel;

import java.util.List;

public interface StatusGroupService {
    List<StatusGroupedModel> getStatusGrouped();
}
