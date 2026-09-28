package com.eldercare.modules.risk_incident.incident_tracking.repository;

import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {

    @Query(value = "SELECT i FROM IncidentEntity i LEFT JOIN FETCH i.resident r LEFT JOIN FETCH r.bed b LEFT JOIN FETCH b.room rm LEFT JOIN FETCH i.severity s",
           countQuery = "SELECT COUNT(i) FROM IncidentEntity i")
    Page<IncidentEntity> findAll(Pageable pageable);

    @Query("SELECT i FROM IncidentEntity i LEFT JOIN FETCH i.resident r LEFT JOIN FETCH r.bed b LEFT JOIN FETCH b.room rm LEFT JOIN FETCH i.severity s LEFT JOIN FETCH i.reporter u WHERE i.id = :id")
    IncidentEntity findWithDetailById(Long id);
}