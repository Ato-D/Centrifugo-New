package com.example.Centrifugo.setup.service;

import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.SetupDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface SetupService {

    ResponseEntity<ResponseDTO> findAllSetup(Map<String, String> params);

    ResponseEntity<ResponseDTO> findById(UUID id);

    ResponseEntity<ResponseDTO> createSetup(SetupDTO setupDto);

    ResponseEntity<ResponseDTO> updateSetup(UUID id, SetupDTO setupDto);

    ResponseEntity<ResponseDTO> disableSetup(UUID id, SetupDTO setupDto);
}
