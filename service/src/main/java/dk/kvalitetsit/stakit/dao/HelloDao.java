package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.HelloEntity;

import java.util.List;

public interface HelloDao {
    void insert(HelloEntity helloEntity);

    List<HelloEntity> findAll();
}