package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.GroupModel;
import org.openapitools.model.Group;
import org.openapitools.model.GroupInput;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupMapper {
    public static GroupModel mapCreateGroup(GroupInput from) {
        return GroupModel.createInstance(from.getName(), from.getDisplayOrder());
    }

    public static GroupModel mapUpdateGroup(UUID uuid, GroupInput from) {
        return new GroupModel(uuid, from.getName(), from.getDisplayOrder());
    }

    public static List<org.openapitools.model.Group> mapGetGroups(List<GroupModel> from) {
        return from.stream().map(GroupMapper::mapGroup).collect(Collectors.toList());
    }

    public static Group mapGroup(GroupModel groupModel) {
        return new Group()
                .displayOrder(groupModel.displayOrder())
                .id(groupModel.uuid())
                .name(groupModel.name());
    }
}
