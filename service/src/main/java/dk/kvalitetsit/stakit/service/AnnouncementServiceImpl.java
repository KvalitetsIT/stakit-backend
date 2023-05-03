package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.AnnouncementDao;
import dk.kvalitetsit.stakit.service.mapper.AnnouncementMapper;
import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementDao announcementDao;
    private final MailQueueService mailQueueService;

    public AnnouncementServiceImpl(AnnouncementDao announcementDao, MailQueueService mailQueueService) {
        this.announcementDao = announcementDao;
        this.mailQueueService = mailQueueService;
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
        var id = announcementDao.insert(AnnouncementMapper.mapModel(uuid, announcementModel));
        mailQueueService.queueAnnouncementMail(id);

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

    @Override
    public List<AnnouncementModel> getAllAnnouncements() {
        return announcementDao.getAnnouncements().stream().map(AnnouncementMapper::mapEntity).collect(Collectors.toList());
    }


}
