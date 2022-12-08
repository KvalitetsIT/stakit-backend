package dk.kvalitetsit.stakit.service.mapper;

import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;
import dk.kvalitetsit.stakit.service.model.Announcement;

import java.util.UUID;

public class AnnouncementMapper {
    public static Announcement mapEntity(AnnouncementEntity announcement) {
        return new Announcement(announcement.uuid(), announcement.fromDatetime(), announcement.toDatetime(), announcement.subject(), announcement.message());
    }

    public static AnnouncementEntity mapModel(Announcement announcement) {
        return AnnouncementEntity.createInstance(announcement.uuid(), announcement.fromDatetime(), announcement.toDatetime(), announcement.subject(), announcement.message());
    }

    public static AnnouncementEntity mapModel(UUID uuid, Announcement announcement) {
        return AnnouncementEntity.createInstance(uuid, announcement.fromDatetime(), announcement.toDatetime(), announcement.subject(), announcement.message());
    }

}
