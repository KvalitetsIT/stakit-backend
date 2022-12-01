package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.Group;
import org.openapitools.model.GroupUpdate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupMapper {
    public static Group mapCreateGroup(GroupUpdate from) {
        return Group.createInstance(from.getName(), from.getDisplayOrder());
    }

    public static Group mapUpdateGroup(UUID uuid, GroupUpdate from) {
        return new Group(uuid, from.getName(), from.getDisplayOrder());
    }

    public static List<org.openapitools.model.Group> mapGetGroups(List<Group> from) {
        return from.stream().map(x -> {
            var g = new org.openapitools.model.Group();
            g.setId(x.uuid());
            g.setName(x.name());
            g.setDisplayOrder(x.displayOrder());

            return g;
        }).collect(Collectors.toList());
    }
}
