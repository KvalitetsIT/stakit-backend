package dk.kvalitetsit.stakit.dao.entity;

public record StatusConfigurationEntity(Long id, String service, String serviceName, boolean ignoreServiceName, Long groupConfigurationId) {
    public static StatusConfigurationEntity createInstance(String service, String serviceName, boolean ignoreServiceName, Long groupConfigurationId) {
        return new StatusConfigurationEntity(null, service, serviceName, ignoreServiceName, groupConfigurationId);
    }
}
