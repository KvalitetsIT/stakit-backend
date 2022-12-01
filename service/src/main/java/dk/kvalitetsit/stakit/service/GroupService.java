package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.Group;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    UUID createGroup(Group group);

    boolean updateGroup(Group group);

    List<Group> getGroups();
}
