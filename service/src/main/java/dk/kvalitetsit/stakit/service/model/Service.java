package dk.kvalitetsit.stakit.service.model;

import java.util.UUID;

public record Service(String name, String serviceIdentifier, boolean ignoreServiceName, UUID group, UUID uuid) {
}
