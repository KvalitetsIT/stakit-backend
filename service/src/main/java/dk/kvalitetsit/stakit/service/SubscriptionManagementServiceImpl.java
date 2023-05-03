package dk.kvalitetsit.stakit.service;


import dk.kvalitetsit.stakit.dao.MailSubscriptionGroupDao;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import dk.kvalitetsit.stakit.service.mapper.SubscriptionMapper;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SubscriptionManagementServiceImpl implements SubscriptionManagementService{
    private final MailSubscriptionGroupDao mailSubscriptionGroupDao;

    public SubscriptionManagementServiceImpl(MailSubscriptionGroupDao mailSubscriptionGroupDao) {
        this.mailSubscriptionGroupDao = mailSubscriptionGroupDao;
    }

    @Override
    public List<SubscriptionModel> getSubscriptions() {
        HashMap<UUID, SubscriptionModel> subs = new LinkedHashMap<>();
        var response = mailSubscriptionGroupDao.getSubscriptions();
        response.forEach(sub -> {
            if(subs.containsKey(sub.subUuid()))  {
                var oldSub = subs.get(sub.subUuid());
                subs.put(sub.subUuid(), merge(oldSub, SubscriptionMapper.mapSubscription(sub)));
            }
            else subs.put(sub.subUuid(), SubscriptionMapper.mapSubscription(sub));
        });
        return subs.values().stream().toList();
    }

    @Override
    public Optional<SubscriptionModel> getSubscription(UUID uuid) {

        var response = mailSubscriptionGroupDao.getSubscriptionByUuid(uuid);

        if (response.size() == 0 ) return Optional.empty();

        List<UUID> groups = response.stream().map(SubscriptionGroupEntity::groupUuid).filter(Objects::nonNull).collect(Collectors.toList());

        return Optional.of( new SubscriptionModel(
                response.get(0).subUuid(),
                response.get(0).email(),
                groups,
                response.get(0).announcements()
        ));
    }

    private static SubscriptionModel merge(SubscriptionModel sub1, SubscriptionModel sub2) {
        return new SubscriptionModel(sub1.uuid(), sub1.email(), Stream.concat(sub1.groups().stream(), sub2.groups().stream()).distinct().toList(), sub1.announcements());
    }



}
