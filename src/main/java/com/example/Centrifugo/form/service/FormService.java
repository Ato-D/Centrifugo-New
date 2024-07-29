package com.example.Centrifugo.form.service;

import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

public interface FormService {

    ResponseEntity<ResponseDTO> findAllForms(Map<String, String> params);

    ResponseEntity<ResponseDTO> findById(UUID id);

    ResponseEntity<ResponseDTO> saveForm(FormDTO formDTO);

    ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO);





}
