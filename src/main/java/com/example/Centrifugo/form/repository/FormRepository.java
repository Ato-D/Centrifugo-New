package com.example.Centrifugo.form.repository;


import com.example.Centrifugo.form.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FormRepository extends JpaRepository<Form, UUID> {

}
