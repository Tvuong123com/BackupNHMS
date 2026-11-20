package com.eldercare.modules.careplan_management.careplan_design.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CareGoalMapper;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CarePlanMapper;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareGoalSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCarePlanRepository;
import com.eldercare.modules.careplan_management.careplan_design.service.ICareGoalRepository;

import jakarta.transaction.Transactional;

@Repository
public class CareGoalRepositoryImpl implements ICareGoalRepository {
    private final JpaCareGoalRepository jpaCareGoalRepository;
    private final JpaCarePlanRepository jpaCarePlanRepository;

    public CareGoalRepositoryImpl(JpaCareGoalRepository jpaCareGoalRepository,
                                  JpaCarePlanRepository jpaCarePlanRepository) {
        this.jpaCareGoalRepository = jpaCareGoalRepository;
        this.jpaCarePlanRepository = jpaCarePlanRepository;
    }

    @Override
    @Transactional()
    public CareGoalEntity saveOne(CareGoalEntity careGoalEntity, CarePlanEntity carePlanEntity) {
        CareGoalSchema schema = CareGoalMapper.toSchema(careGoalEntity);
        var plan = jpaCarePlanRepository.findById((long) carePlanEntity.getId())
                .orElseThrow(() -> new IllegalArgumentException("Care plan not found"));
        if ("ARCHIVED".equals(plan.getStatus()) || Boolean.TRUE.equals(plan.getIsDeleted())) {
            throw new IllegalStateException("Cannot add a goal to an archived care plan");
        }
        schema.setCarePlan(plan);
        CareGoalSchema schemaAfterInserted = this.jpaCareGoalRepository.save(schema);
        plan.setStatus("PENDING_REVIEW");
        plan.setUpdatedAt(java.time.OffsetDateTime.now());
        jpaCarePlanRepository.save(plan);
        return CareGoalMapper.toEntity(schemaAfterInserted);
    }

}
