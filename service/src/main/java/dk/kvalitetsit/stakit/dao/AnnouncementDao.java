package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;

import java.util.Optional;
import java.util.UUID;

public interface AnnouncementDao {
    long insert(AnnouncementEntity announcementEntity);
    Optional<AnnouncementEntity> getByUuid(UUID uuid);
    boolean updateByUuid(AnnouncementEntity announcementEntity);
    boolean deleteByUuid(UUID uuid);
}
