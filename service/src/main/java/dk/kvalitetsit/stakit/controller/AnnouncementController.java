package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundException;
import dk.kvalitetsit.stakit.controller.mapper.AnnouncementMapper;
import dk.kvalitetsit.stakit.service.AnnouncementService;
import org.openapitools.api.AnnouncementManagementApi;
import org.openapitools.model.Announcement;
import org.openapitools.model.AnnouncementCreate;
import org.openapitools.model.AnnouncementUpdate;
import org.openapitools.model.CreateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AnnouncementController implements AnnouncementManagementApi {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @Override
    public ResponseEntity<CreateResponse> v1AnnouncementsPost(AnnouncementCreate announcementCreate) {
        var result = announcementService.createAnnouncement(AnnouncementMapper.mapToService(announcementCreate));

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateResponse(result));
    }

    @Override
    public ResponseEntity<Void> v1AnnouncementsUuidDelete(UUID uuid) {
        var deleted = announcementService.deleteAnnouncement(uuid);

        if(!deleted) {
            throw new ResourceNotFoundException("Announcement with uuid %s not found.".formatted(uuid));
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Announcement> v1AnnouncementsUuidGet(UUID uuid) {
        var result = announcementService.getAnnouncement(uuid);

        if(result.isEmpty()) {
            throw new ResourceNotFoundException("Announcement with uuid %s not found.".formatted(uuid));
        }

        return ResponseEntity.ok(AnnouncementMapper.mapToApi(result.get()));
    }

    @Override
    public ResponseEntity<Void> v1AnnouncementsUuidPut(UUID uuid, AnnouncementUpdate announcementUpdate) {
        var updated = announcementService.updateAnnouncement(AnnouncementMapper.mapToService(uuid, announcementUpdate));

        if(!updated) {
            throw new ResourceNotFoundException("Announcement with uuid %s not found.".formatted(uuid));
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Announcement>> v1AnnouncementsGet() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements().stream().map(AnnouncementMapper::mapToApi).collect(Collectors.toList()));
    }
}
