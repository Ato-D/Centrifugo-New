package com.example.Centrifugo.dto;



import com.example.Centrifugo.form.FormDetails;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormDTO {

    private UUID id;

    private String name;

    private int version;

    private List<FormDetails> formDetails;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID createdBy;

    private UUID updatedBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();










}
