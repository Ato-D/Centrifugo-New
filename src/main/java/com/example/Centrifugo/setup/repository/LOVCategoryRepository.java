package com.example.Centrifugo.setup.repository;

import com.example.Centrifugo.setup.model.LOVCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LOVCategoryRepository extends JpaRepository<LOVCategory, UUID> {
}
