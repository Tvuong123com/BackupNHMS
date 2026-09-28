package com.eldercare.modules.careplan_management.careplan_design.mapper;

import java.util.List;

import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.resident_info.CarePlanResidentInfoEntity;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareGoalSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CarePlanSchema;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

public class CarePlanMapper {
        public static CarePlanEntity toEntity(CarePlanSchema schema) {
                if (schema == null)
                        return null;

                CarePlanEntity newEntity = new CarePlanEntity();
                newEntity.setCreatedBy(schema.getCreatedBy() != null ? schema.getCreatedBy().getId().intValue() : 1);
                newEntity.setId(schema.getId() != null ? schema.getId().intValue() : 0);
                if (schema.getStatus() != null) {
                        try {
                                newEntity.setStatus(CarePlanStatusEnum.valueOf(schema.getStatus()));
                        } catch (Exception e) {
                                newEntity.setStatus(CarePlanStatusEnum.DRAFT);
                        }
                }
                
                ResidentEntity residentEntity = schema.getResident();
                if (residentEntity != null) {
                        BedEntity bed = residentEntity.getBed();
                        newEntity.setResident(new CarePlanResidentInfoEntity(
                                        residentEntity.getId() != null ? residentEntity.getId().intValue() : 0,
                                        getResidentFullName(residentEntity),
                                        residentEntity.getDateOfBirth(),
                                        bed != null && bed.getRoom() != null ? bed.getRoom().getRoomNumber() : null,
                                        bed != null ? bed.getBedNumber() : null,
                                        Boolean.TRUE.equals(schema.getSignificantChangeFlag())));
                } else {
                        newEntity.setResident(new CarePlanResidentInfoEntity(0, "Unknown Resident", null, null, null, false));
                }

                if (schema.getListCareGoal() != null) {
                        newEntity.setListCareGoal(
                                        schema.getListCareGoal().stream().map(careGoalschema -> new CareGoalEntity(
                                                        careGoalschema.getId() != null ? careGoalschema.getId().intValue() : 0,
                                                        careGoalschema.getTitle(),
                                                        careGoalschema.getDescription(),
                                                        careGoalschema.getStatus(),
                                                        careGoalschema.getListCareIntervention() != null
                                                                        ? careGoalschema.getListCareIntervention().stream().map(
                                                                                        careInterventionSchema -> new CareInterventionEntity(
                                                                                                        careInterventionSchema.getId() != null ? careInterventionSchema.getId().intValue() : 0,
                                                                                                        careInterventionSchema.getTitle(),
                                                                                                        careInterventionSchema.getAssignedRole()))
                                                                                        .toList()
                                                                        : List.of()))
                                                        .toList());
                }

                newEntity.setCreatedAt(schema.getCreatedAt());
                newEntity.setUpdatedAt(schema.getUpdatedAt());
                newEntity.setIsDeleted(Boolean.TRUE.equals(schema.getIsDeleted()));
                newEntity.setSignificantFlag(Boolean.TRUE.equals(schema.getSignificantChangeFlag()));
                newEntity.setLastReviewDateTime(schema.getLastReviewedDateTime());
                newEntity.setLastReviewBy(schema.getLastReviewedBy() != null ? schema.getLastReviewedBy().getId().toString() : null);

                return newEntity;
        }

        public static CarePlanSchema toSchema(CarePlanEntity entity) {
                if (entity == null)
                        return null;

                CarePlanSchema schema = new CarePlanSchema();
                ResidentEntity resident = new ResidentEntity();
                resident.setId((long) entity.getResident().getId());
                UserEntity user = new UserEntity();
                user.setId((long) entity.getCreatedBy());
                schema.setResident(resident);
                if (entity.getId() > 0) {
                        schema.setId((long) entity.getId());
                }

                List<CareGoalSchema> goalSchemas = entity.getListCareGoal()
                                .stream()
                                .map(goal -> {
                                        CareGoalSchema goalSchema = new CareGoalSchema();

                                        goalSchema.setTitle(goal.getName());
                                        goalSchema.setDescription(goal.getDescription());
                                        goalSchema.setStatus(goal.getStatus());

                                        goalSchema.setCarePlan(schema);

                                        return goalSchema;
                                })
                                .toList();

                schema.setStatus(entity.getStatus().name());
                schema.setSignificantChangeFlag(entity.getSignificantFlag());
                schema.setIsDeleted(entity.getIsDeleted());
                schema.setCreatedAt(entity.getCreatedAt());
                schema.setUpdatedAt(entity.getUpdatedAt());
                schema.setCreatedBy(user);
                schema.setListCareGoal(goalSchemas);
                return schema;
        }

        private static String getResidentFullName(ResidentEntity residentEntity) {
                StringBuilder fullname = new StringBuilder();

                if (residentEntity.getFirstName() != null) {
                        fullname.append(residentEntity.getFirstName());
                }

                if (residentEntity.getMiddleName() != null) {
                        if (!fullname.isEmpty()) {
                                fullname.append(" ");
                        }
                        fullname.append(residentEntity.getMiddleName());
                }

                if (residentEntity.getLastName() != null) {
                        if (!fullname.isEmpty()) {
                                fullname.append(" ");
                        }
                        fullname.append(residentEntity.getLastName());
                }

                return fullname.toString();
        }
}
