package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.GroupModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {
    UUID createGroup(GroupModel groupModel);

    boolean updateGroup(GroupModel groupModel);

    List<GroupModel> getGroups();

    boolean deleteGroup(UUID uuid);

    Optional<GroupModel> getGroup(UUID uuid);
}
