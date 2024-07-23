package com.example.Centrifugo.dto;

import com.example.setups.Form;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String inputType;

    private HashMap<String, Object> option = new HashMap<>();

    private Map<Object, Object> keyValue = new HashMap<>();

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID createdBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();

    private List<Form> form;











}
