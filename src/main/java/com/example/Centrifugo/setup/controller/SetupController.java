package com.example.Centrifugo.setup.controller;


import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.SetupDTO;
import com.example.Centrifugo.setup.service.SetupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.example.Centrifugo.config.SecurityConfig.CONTEXT_PATH;

@RestController
@RequestMapping(CONTEXT_PATH)
@AllArgsConstructor
@Slf4j
public class SetupController {

    @Autowired
    private final SetupService setupService;


    /**
     * Handles a GET request to retrieve all setups.
     *
     * @return ResponseEntity containing the ResponseDTO with the list of setups.
     *
     */
    @GetMapping("/find-all-setups")
    public ResponseEntity<ResponseDTO> findAllSetup(@RequestParam(defaultValue = "{}") Map<String, String> params) {
        return setupService.findAllSetup(params);
    }

    /**
     * Handles a GET request to retrieve a setup by its ID.
     *
     * @param id of the setup to retrieve.
     * @return ResponseEntity containing the ResponseDTO with the requested form.
     */
    @GetMapping("/get-setup-by-id/{id}")
    public ResponseEntity<ResponseDTO> findById(@PathVariable(name = "id") UUID id) {
        return setupService.findById(id);
    }

    @PostMapping("/create-setup")
    public ResponseEntity<ResponseDTO> createSetup(@RequestBody SetupDTO setupDto) {
        return setupService.createSetup(setupDto);
    }

    @PutMapping({"/update-setup/{id}"})
    public ResponseEntity<ResponseDTO> updateSetup(@PathVariable(name = "id") UUID id, @RequestBody SetupDTO setupDto) {
        return setupService.updateSetup(id, setupDto);
    }

    @PutMapping("/disable-setup/{id}")
    public ResponseEntity<ResponseDTO> disableSetup(@RequestBody SetupDTO setupDto, @PathVariable(name = "id") UUID id ){
        return setupService.disableSetup(id,setupDto);
    }
}
