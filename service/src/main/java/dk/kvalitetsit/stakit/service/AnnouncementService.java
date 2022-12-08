package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.Announcement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementService {
    Optional<Announcement> getAnnouncement(UUID uuid);
    UUID createAnnouncement(Announcement announcement);
    boolean deleteAnnouncement(UUID uuid);
    boolean updateAnnouncement(Announcement announcement);
    List<Announcement> getAnnouncements();
}
