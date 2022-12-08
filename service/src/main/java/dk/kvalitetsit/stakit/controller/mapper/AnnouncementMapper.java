package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.Announcement;
import org.openapitools.model.AnnouncementCreate;
import org.openapitools.model.AnnouncementUpdate;

import java.util.UUID;

public class AnnouncementMapper {
    public static Announcement mapToService(AnnouncementCreate announcementCreate) {
        return new Announcement(null, announcementCreate.getFromDatetime(), announcementCreate.getToDatetime(), announcementCreate.getSubject(), announcementCreate.getMessage());
    }

    public static org.openapitools.model.Announcement mapToApi(Announcement announcement) {
        return new org.openapitools.model.Announcement()
                .fromDatetime(announcement.fromDatetime())
                .toDatetime(announcement.toDatetime())
                .uuid(announcement.uuid())
                .subject(announcement.subject())
                .message(announcement.message());
    }

    public static Announcement mapToService(UUID uuid, AnnouncementUpdate announcementUpdate) {
        return new Announcement(uuid, announcementUpdate.getFromDatetime(), announcementUpdate.getToDatetime(), announcementUpdate.getSubject(), announcementUpdate.getMessage());
    }
}
