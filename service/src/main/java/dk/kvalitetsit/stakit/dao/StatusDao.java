package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusEntity;

import java.util.List;

public interface StatusDao {
    void insertUpdate(StatusEntity statusEntity);

    List<StatusEntity> findAll();
}