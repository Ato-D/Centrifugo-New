package com.example.Centrifugo.dto;

import com.example.Centrifugo.enums.Constraints;
import com.example.Centrifugo.enums.InputType;
import com.example.Centrifugo.form.Form;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormDetailsDTO {

    private UUID id;

    private String label;

    private Map<String, Object> option;


    private InputType inputType;

    private Constraints constraints;

    private String key;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID createdBy;

    private UUID updatedBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();

    private List<Form> form;











}
