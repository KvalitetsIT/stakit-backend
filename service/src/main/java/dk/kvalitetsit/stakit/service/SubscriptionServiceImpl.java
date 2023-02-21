package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionGroupDao;
import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.mapper.SubscriptionMapper;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class SubscriptionServiceImpl implements SubscriptionService {
    private final GroupConfigurationDao groupConfigurationDao;
    private final MailSubscriptionDao subscriptionDao;
    private final MailSubscriptionGroupDao mailSubscriptionGroupDao;
    private final MailSenderService mailSenderService;
    private String baseUrl;

    public SubscriptionServiceImpl(GroupConfigurationDao groupConfigurationDao, MailSubscriptionDao subscriptionDao, MailSubscriptionGroupDao mailSubscriptionGroupDao, MailSenderService mailSenderService, String baseUrl) {
        this.groupConfigurationDao = groupConfigurationDao;
        this.subscriptionDao = subscriptionDao;
        this.mailSubscriptionGroupDao = mailSubscriptionGroupDao;
        this.mailSenderService = mailSenderService;
        this.baseUrl = baseUrl;
    }

    @Override
    @Transactional
    public UUID subscribe(SubscriptionModel mapSubscriptionModel) {
        var subscriptionEntity = SubscriptionMapper.mapSubscription(mapSubscriptionModel);
        mailSubscriptionGroupDao.deleteByEmail(mapSubscriptionModel.email());
        subscriptionDao.deleteByEmail(mapSubscriptionModel.email());
        var subscriptionId = subscriptionDao.insert(subscriptionEntity);

        mapSubscriptionModel.groups().stream()
                .map(x -> MailSubscriptionGroupsEntity.createInstance(subscriptionId, groupConfigurationDao.findByUuid(x).orElseThrow(() -> new InvalidDataException("Group not found: %s".formatted(x))).id()))
                .forEach(mailSubscriptionGroupDao::insert);

        StringBuilder bodyBuilder = new StringBuilder()
                .append("For at bekræfte din tilmelding til statusopdateringer klik på nedenstående link.")
                .append("\n")
                .append("\n")
                .append(baseUrl)
                .append("/subscribe/")
                .append(subscriptionEntity.confirmIdentifier());

        mailSenderService.sendMail(mapSubscriptionModel.email(), "Bekræft tilmelding til statusopdateringer", bodyBuilder.toString());

        return subscriptionEntity.uuid();
    }

    @Override
    @Transactional
    public void confirmSubscription(UUID confirmationUuid) {
        subscriptionDao.updateConfirmedByConfirmationUuid(confirmationUuid);
    }
}
