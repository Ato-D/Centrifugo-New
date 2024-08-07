package com.example.Centrifugo.form.controller;


import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.ResponseDTO;

import com.example.Centrifugo.form.service.FormService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.example.Centrifugo.config.SecurityConfig.CONTEXT_PATH;


/**
 * This class represents the FormController, which handles HTTP requests related to form operations.
 * It provides various endpoints for form management and interacts with the FormService for business logic.
 *
 * @RestController Indicates that this class is a restcontroller and combines @Controller and @ResponseBody.
 * @RequestMapping (api/v1/form) Specifies the base URL path for all endpoints in this restcontroller.
 * @RequiredArgsConstructor Lombok's annotation to generate a constructor with required fields for dependency injection.
 * @Slf4j Lombok's annotation to generate a logger field for this class. *
 * @author Derrick Donkoh
 * @createdAt 23rd July 2024
 * @modified
 * @modifiedAt
 * @modifiedBy
 */
@CrossOrigin
@RestController
@RequestMapping(CONTEXT_PATH)
@AllArgsConstructor
@Slf4j
public class FormController {

    private final FormService formService;

    /**
     * Handles a GET request to retrieve all forms.
     *
     * @return ResponseEntity containing the ResponseDTO with the list of forms.
     *
     */
    @GetMapping("/findAllForms")
    public ResponseEntity<ResponseDTO> getAllForms(@RequestParam(defaultValue = "{}")Map<String, String> params) {
        return formService.findAllForms(params);
    }


    /**
     * Handles a GET request to retrieve a form by its ID.
     *
     * @param id The ID of the form to retrieve.
     * @return ResponseEntity containing the ResponseDTO with the requested form.
     */
    @GetMapping("form/{id}")
    public ResponseEntity<ResponseDTO> getFormById(@PathVariable UUID id){
        return formService.findById(id);
    }


    @PostMapping("/create-form")
    public ResponseEntity<ResponseDTO> saveForm(@RequestBody FormDTO formDTO){
        return formService.saveForm(formDTO);
    }


    /**
     * Handles a PUT request to update an existing form.
     *
     * @param formDTO The updated FormDTO containing information to update form.
     * @param id The ID of the form to be updated.
     * @return ResponseEntity containing the ResponseDTO with the updated form.
     */
    @PutMapping({"/{id}"})
    public ResponseEntity<ResponseDTO> updateForm(@PathVariable(name = "id") UUID id, @RequestBody FormDTO formDTO) {
        return formService.update(id, formDTO);
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<ResponseDTO> disableForm(@RequestBody FormDTO formDTO, @PathVariable(name = "id") UUID id ){
        return formService.disable(id, formDTO);
    }


}
