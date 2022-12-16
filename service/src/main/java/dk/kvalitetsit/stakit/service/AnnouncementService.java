package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.AnnouncementModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementService {
    Optional<AnnouncementModel> getAnnouncement(UUID uuid);
    UUID createAnnouncement(AnnouncementModel announcementModel);
    boolean deleteAnnouncement(UUID uuid);
    boolean updateAnnouncement(AnnouncementModel announcementModel);
    List<AnnouncementModel> getAnnouncements();
}
