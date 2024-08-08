package com.example.Centrifugo.setup.controller;


import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.LOVDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.setup.model.LOV;
import com.example.Centrifugo.setup.repository.LOVRepository;
import com.example.Centrifugo.setup.service.LOVService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.example.Centrifugo.config.SecurityConfig.CONTEXT_PATH;

@RestController
@RequestMapping(CONTEXT_PATH)
@AllArgsConstructor
@Slf4j
public class LOVController {

    private final LOVService lovService;


    /**
     * Handles a GET request to retrieve all values.
     *
     * @return ResponseEntity containing the ResponseDTO with the list of values.
     *
     */
    @GetMapping("/find-all-values")
    public ResponseEntity<ResponseDTO> getAllValues(@RequestParam(defaultValue = "{}") Map<String, String> params) {
        return lovService.findAllValues(params);
    }

    /**
     * Handles a GET request to retrieve a value by its category ID.
     *
     * @param categoryId The ID of the form to retrieve.
     * @return ResponseEntity containing the ResponseDTO with the requested form.
     */
    @GetMapping("/get-values-by-categoryId/{id}")
    public ResponseEntity<ResponseDTO> getValuesByCategoryId(@PathVariable (name = "id")UUID categoryId) {
        return lovService.getValuesByCategoryId(categoryId);
    }

    @PostMapping("/create-value")
    public ResponseEntity<ResponseDTO> createValue(@RequestBody LOVDTO lovdto) {
        return lovService.createValue(lovdto);
    }

    /**
     * Handles a PUT request to update an existing form.
     *
     * @param lovdto The updated LOVDTO containing information to update value.
     * @param id The ID of the value to be updated.
     * @return ResponseEntity containing the ResponseDTO with the updated value.
     */
    @PutMapping({"/update-value/{id}"})
    public ResponseEntity<ResponseDTO> updateValue(@PathVariable(name = "id") UUID id, @RequestBody LOVDTO lovdto) {
        return lovService.updateValue(id, lovdto);
    }

    /**
     * Handles a PUT request to disable a value.
     *
     * @param lovdto The LOVDTO containing enabled field to disable or enable tyre brand.
     * @param id The ID of the tyre brand to be disabled or enabled.
     * @return ResponseEntity containing the ResponseDTO.
     */
    @PutMapping("/disable-value/{id}")
    public ResponseEntity<ResponseDTO> disableValue(@RequestBody LOVDTO lovdto, @PathVariable(name = "id") UUID id ){
        return lovService.disableValue(id,lovdto);
    }
}
