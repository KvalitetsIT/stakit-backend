package dk.kvalitetsit.stakit.dao.entity;

public record ServiceConfigurationEntity(Long id, String service, String name, boolean ignoreServiceName, Long groupConfigurationId) {
    public static ServiceConfigurationEntity createInstance(String service, String name, boolean ignoreServiceName, Long groupConfigurationId) {
        return new ServiceConfigurationEntity(null, service, name, ignoreServiceName, groupConfigurationId);
    }
}
