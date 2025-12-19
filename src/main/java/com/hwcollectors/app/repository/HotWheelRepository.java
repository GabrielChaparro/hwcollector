package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.HotWheel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotWheelRepository extends JpaRepository<HotWheel, Long> {  // ← Long ID
    Optional<HotWheel> findByCode(String code);

    List<HotWheel> findBySeries(String series);
}
