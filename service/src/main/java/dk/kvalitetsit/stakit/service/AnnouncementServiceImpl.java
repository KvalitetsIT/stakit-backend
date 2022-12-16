package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.AnnouncementDao;
import dk.kvalitetsit.stakit.service.mapper.AnnouncementMapper;
import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Optional<AnnouncementModel> getAnnouncement(UUID uuid) {
        return announcementDao.getByUuid(uuid).map(AnnouncementMapper::mapEntity);
    }

    @Override
    @Transactional
    public UUID createAnnouncement(AnnouncementModel announcementModel) {
        var uuid = UUID.randomUUID();
        announcementDao.insert(AnnouncementMapper.mapModel(uuid, announcementModel));

        return uuid;
    }

    @Override
    @Transactional
    public boolean deleteAnnouncement(UUID uuid) {
        return announcementDao.deleteByUuid(uuid);
    }

    @Override
    @Transactional
    public boolean updateAnnouncement(AnnouncementModel announcementModel) {
        return announcementDao.updateByUuid(AnnouncementMapper.mapModel(announcementModel));
    }

    @Override
    @Transactional
    public List<AnnouncementModel> getAnnouncements() {
        var announcements = announcementDao.getAnnouncements(OffsetDateTime.now());

        return announcements.stream().map(AnnouncementMapper::mapEntity).toList();
    }
}
