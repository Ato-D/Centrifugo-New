package com.example.Centrifugo.setup.repository;

import com.example.Centrifugo.setup.model.LOV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LOVRepository extends JpaRepository<LOV, UUID> {

    @Query("""
        select l
        from LOV l 
        where l.setupId = :id
    """)
    LOV findBySetupId(@Param("id") UUID id);
}
