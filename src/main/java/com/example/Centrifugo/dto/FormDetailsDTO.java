package com.example.Centrifugo.dto;

import com.example.Centrifugo.enums.Constraints;
import com.example.Centrifugo.enums.FieldType;
import com.example.Centrifugo.form.Form;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormDetailsDTO {

    private UUID id;

    private int index;

    private String fieldLabel;

    private Map<String, Object> fieldOptions;

    private Boolean isRequired;

    private String defaultValue;

    private String placeholder;

    private FieldType fieldType;

    private Constraints constraints;

    private String key;

    private List<Form> form;

    private UUID createdBy;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID updatedBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();













}
