package dk.kvalitetsit.stakit.service.model;

import java.util.UUID;

public record ServiceModel(String name, String serviceIdentifier, boolean ignoreServiceName, UUID group, UUID uuid, String description) {
}
