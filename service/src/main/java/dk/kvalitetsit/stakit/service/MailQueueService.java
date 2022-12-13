package dk.kvalitetsit.stakit.service;

public interface MailQueueService {
    void queueStatusUpdatedMail(long serviceConfigurationId, long serviceStatusId);
}
