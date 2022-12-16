package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import org.openapitools.model.AnnouncementCreate;
import org.openapitools.model.AnnouncementUpdate;
import org.openapitools.model.AnnouncementsToShow;

import java.util.List;
import java.util.UUID;

public class AnnouncementMapper {
    public static AnnouncementModel mapToService(AnnouncementCreate announcementCreate) {
        return new AnnouncementModel(null, announcementCreate.getFromDatetime(), announcementCreate.getToDatetime(), announcementCreate.getSubject(), announcementCreate.getMessage());
    }

    public static org.openapitools.model.Announcement mapToApi(AnnouncementModel announcementModel) {
        return new org.openapitools.model.Announcement()
                .fromDatetime(announcementModel.fromDatetime())
                .toDatetime(announcementModel.toDatetime())
                .uuid(announcementModel.uuid())
                .subject(announcementModel.subject())
                .message(announcementModel.message());
    }

    public static AnnouncementModel mapToService(UUID uuid, AnnouncementUpdate announcementUpdate) {
        return new AnnouncementModel(uuid, announcementUpdate.getFromDatetime(), announcementUpdate.getToDatetime(), announcementUpdate.getSubject(), announcementUpdate.getMessage());
    }

    public static List<AnnouncementsToShow> mapAnnouncementsToShow(List<AnnouncementModel> announcementModels) {
        return announcementModels.stream().map(x -> new AnnouncementsToShow()
                .fromDatetime(x.fromDatetime())
                .toDatetime(x.toDatetime())
                .message(x.message())
                .subject(x.subject())).toList();
    }
}
