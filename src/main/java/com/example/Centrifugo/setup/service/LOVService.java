package com.example.Centrifugo.setup.service;

import com.example.Centrifugo.dto.LOVDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.setup.model.LOV;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface LOVService {

    ResponseEntity<ResponseDTO> findAllValues(Map<String, String> params);

    ResponseEntity<ResponseDTO> getValuesByCategoryId(UUID categoryId);

    ResponseEntity<ResponseDTO> createValue(LOVDTO lovdto);

    ResponseEntity<ResponseDTO> updateValue(UUID id, LOVDTO lovdto);

    ResponseEntity<ResponseDTO> disableValue(UUID id, LOVDTO lovdto);

}
