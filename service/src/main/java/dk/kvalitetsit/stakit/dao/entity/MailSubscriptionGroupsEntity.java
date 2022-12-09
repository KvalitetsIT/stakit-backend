package dk.kvalitetsit.stakit.dao.entity;

public record MailSubscriptionGroupsEntity(Long id, long mailSubscriptionId, long groupConfigurationId) {
    public static MailSubscriptionGroupsEntity createInstance(long mailSubscriptionId, long groupConfigurationId) {
        return new MailSubscriptionGroupsEntity(null, mailSubscriptionId, groupConfigurationId);
    }
}
