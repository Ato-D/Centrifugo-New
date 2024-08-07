package com.example.Centrifugo.form.repository;


import com.example.Centrifugo.form.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FormRepository extends JpaRepository<Form, UUID> {

    @Query("""
        select f
        from Form f
        where f.isEnabled
    """)
    List<Form> findAllEnabledForm();

}
