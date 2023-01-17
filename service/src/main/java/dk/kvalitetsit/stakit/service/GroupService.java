package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.GroupGetModel;
import dk.kvalitetsit.stakit.service.model.GroupModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {
    UUID createGroup(GroupModel groupModel);

    boolean updateGroup(GroupModel groupModel);

    List<GroupGetModel> getGroups();

    boolean deleteGroup(UUID uuid);

    Optional<GroupGetModel> getGroup(UUID uuid);

    boolean patchGroup(UUID groupUuid, List<UUID> serviceList);
}
