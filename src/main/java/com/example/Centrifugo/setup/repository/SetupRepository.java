package com.example.Centrifugo.setup.repository;

import com.example.Centrifugo.setup.model.SetupModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetupRepository extends JpaRepository<SetupModel, UUID> {
}
