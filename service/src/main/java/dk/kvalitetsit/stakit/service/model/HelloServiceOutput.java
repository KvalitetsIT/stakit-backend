package dk.kvalitetsit.stakit.service.model;

import java.time.ZonedDateTime;

public record HelloServiceOutput(String name, ZonedDateTime now) {
}
