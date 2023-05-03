package dk.kvalitetsit.stakit.service.mapper;

import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;
import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import org.openapitools.model.Announcement;

import java.util.UUID;

public class AnnouncementMapper {
    public static AnnouncementModel mapEntity(AnnouncementEntity announcement) {
        return new AnnouncementModel(announcement.uuid(), announcement.fromDatetime(), announcement.toDatetime(), announcement.subject(), announcement.message());
    }

    public static AnnouncementEntity mapModel(AnnouncementModel announcementModel) {
        return AnnouncementEntity.createInstance(announcementModel.uuid(), announcementModel.fromDatetime(), announcementModel.toDatetime(), announcementModel.subject(), announcementModel.message());
    }

    public static AnnouncementEntity mapModel(UUID uuid, AnnouncementModel announcementModel) {
        return AnnouncementEntity.createInstance(uuid, announcementModel.fromDatetime(), announcementModel.toDatetime(), announcementModel.subject(), announcementModel.message());
    }



}
