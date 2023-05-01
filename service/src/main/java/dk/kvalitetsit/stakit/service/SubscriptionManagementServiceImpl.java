package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.MailSubscriptionGroupDao;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;

import java.util.*;

public class SubscriptionManagementServiceImpl implements SubscriptionManagementService{
    private final MailSubscriptionGroupDao mailSubscriptionGroupDao;

    public SubscriptionManagementServiceImpl(MailSubscriptionGroupDao mailSubscriptionGroupDao) {
        this.mailSubscriptionGroupDao = mailSubscriptionGroupDao;
    }

    @Override
    public List<SubscriptionModel> getSubscriptions() {
        HashMap<UUID, SubscriptionModel> subs = new HashMap<>();
        var response = mailSubscriptionGroupDao.getSubscriptions();
        response.forEach(sub -> {
            if(subs.containsKey(sub.subUuid()))  {
                var oldSub = subs.get(sub.subUuid());
                subs.put(sub.subUuid(), SubscriptionModel.createInstance(oldSub, map(sub)));
            }
            else subs.put(sub.subUuid(), map(sub));
        });
        return subs.values().stream().toList();
    }

    private SubscriptionModel map(SubscriptionGroupEntity sub) {
        return new SubscriptionModel(sub.subUuid(), sub.email(), List.of(sub.groupUuid()), sub.announcements());
    }

    @Override
    public Optional<SubscriptionModel> getSubscription(UUID uuid) {
        return Optional.of( map( mailSubscriptionGroupDao.getSubscriptionByUuid(uuid)));

    }

}
