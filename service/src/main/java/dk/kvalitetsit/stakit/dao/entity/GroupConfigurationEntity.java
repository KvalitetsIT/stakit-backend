package dk.kvalitetsit.stakit.dao.entity;

public record GroupConfigurationEntity(Long id, String groupName) {
    public static GroupConfigurationEntity createInstance(String groupName) {
        return new GroupConfigurationEntity(null, groupName);
    }
}
