package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.HotWheel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotWheelRepository extends MongoRepository<HotWheel, String> {
    Optional<HotWheel> findByCode(String code);
    List<HotWheel> findBySeries(String series);
}


