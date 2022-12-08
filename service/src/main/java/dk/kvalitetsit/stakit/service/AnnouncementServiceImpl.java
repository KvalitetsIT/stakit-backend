package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.AnnouncementDao;
import dk.kvalitetsit.stakit.service.mapper.AnnouncementMapper;
import dk.kvalitetsit.stakit.service.model.Announcement;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementDao announcementDao;

    public AnnouncementServiceImpl(AnnouncementDao announcementDao) {
        this.announcementDao = announcementDao;
    }

    @Override
    public Optional<Announcement> getAnnouncement(UUID uuid) {
        return announcementDao.getByUuid(uuid).map(AnnouncementMapper::mapEntity);
    }

    @Override
    public UUID createAnnouncement(Announcement announcement) {
        var uuid = UUID.randomUUID();
        announcementDao.insert(AnnouncementMapper.mapModel(uuid, announcement));

        return uuid;
    }

    @Override
    public boolean deleteAnnouncement(UUID uuid) {
        return announcementDao.deleteByUuid(uuid);
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        return announcementDao.updateByUuid(AnnouncementMapper.mapModel(announcement));
    }

    @Override
    public List<Announcement> getAnnouncements() {
        var announcements = announcementDao.getAnnouncements(OffsetDateTime.now());

        return announcements.stream().map(AnnouncementMapper::mapEntity).toList();
    }
}
