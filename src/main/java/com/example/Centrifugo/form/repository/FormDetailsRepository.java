package com.example.Centrifugo.form.repository;

import com.example.Centrifugo.form.FormDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FormDetailsRepository extends JpaRepository<FormDetails, UUID> {
}
