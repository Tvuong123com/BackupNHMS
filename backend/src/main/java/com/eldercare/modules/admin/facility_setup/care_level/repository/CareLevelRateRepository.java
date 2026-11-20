package com.eldercare.modules.admin.facility_setup.care_level.repository;

import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



import java.util.List;

public interface CareLevelRateRepository 
        extends JpaRepository<CareLevelRateEntity, Long> {

    List<CareLevelRateEntity> findByCareLevel_Id(Long careLevelId);

    @Query("select r from CareLevelRateEntity r join fetch r.careLevel")
    List<CareLevelRateEntity> findAllWithCareLevel();

}

