package com.example.Centrifugo.setup.service;

import com.example.Centrifugo.dto.LOVCategoryDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.setup.model.LOVCategory;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface LOVCategoryService {

    ResponseEntity<ResponseDTO> findAllCategories(Map<String, String> params);

    ResponseEntity<ResponseDTO> findById(UUID id);
    ResponseEntity<ResponseDTO> createCategory(LOVCategoryDTO lovCategoryDTO);

    ResponseEntity<ResponseDTO> updateCategory(UUID id, LOVCategoryDTO lovCategoryDTO);

    ResponseEntity<ResponseDTO> disableCategory(UUID id, LOVCategoryDTO lovCategoryDTO);
}
