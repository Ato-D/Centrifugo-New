package com.example.Centrifugo.setup.repository;

import com.example.Centrifugo.setup.model.LOV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LOVRepository extends JpaRepository<LOV, UUID> {
}
