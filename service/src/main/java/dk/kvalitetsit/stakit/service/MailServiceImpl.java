package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.MailSubscriptionDao;
import dk.kvalitetsit.stakit.service.model.Message;

public class MailServiceImpl implements MailService {
    private final MailSubscriptionDao mailSubscriptionDao;
    private final MailSenderService mailSenderService;


    public MailServiceImpl(MailSubscriptionDao mailSubscriptionDao, MailSenderService mailSenderService) {
        this.mailSubscriptionDao = mailSubscriptionDao;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public void queueStatusUpdatedMail(long serviceConfigurationId, long serviceStatusId) {
        var mails = mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(serviceConfigurationId);

        mails.stream()
                .map(x -> new Message(x.email(), "Service status change", "This is a mail text"))
                .forEach(this::processMail);
    }

    @Override
    public void processMail(Message message) {
        mailSenderService.sendMail(message.to(), message.subject(), message.text());
    }
}
