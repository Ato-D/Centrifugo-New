package com.example.Centrifugo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LOVDTO {

    private UUID id;

    private String name;

    private boolean isEnabled;

    private UUID categoryid;

    private UUID createdBy;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID updatedBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();
}
