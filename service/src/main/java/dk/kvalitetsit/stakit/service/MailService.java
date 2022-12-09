package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.Message;

public interface MailService {
    void queueStatusUpdatedMail(long serviceConfigurationId, long serviceStatusId);

    void processMail(Message message);
}
