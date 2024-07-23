package com.stlghana.tbms.uchiha.restcontroller;

import com.stlghana.tbms.uchiha.dto.JobCardDTO;
import com.stlghana.tbms.uchiha.dto.ResponseDTO;
import com.stlghana.tbms.uchiha.service.JobCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.stlghana.tbms.uchiha.config.SecurityConfig.CONTEXT_PATH;

/**
 * This class represents the JobCardController, which handles HTTP requests related to jobCard operations.
 * It provides various endpoints for issue management and interacts with the JobCardService for business logic.
 *
 * @RestController Indicates that this class is a restcontroller and combines @Controller and @ResponseBody.
 * @RequestMapping (CONTEXT_PATH + "/jobCards") Specifies the base URL path for all endpoints in this restcontroller.
 * @RequiredArgsConstructor Lombok's annotation to generate a constructor with required fields for dependency injection.
 * @Slf4j Lombok's annotation to generate a logger field for this class.
 * @Tag (name = "JobCards Endpoints", description = "This contains all endpoints that are used to interact with the jobCard model")
 * Provides OpenAPI documentation for this group of endpoints.
 *
 * @author Prince Amofah
 * @createdAt 10th October 2023
 * @modified
 * @modifiedAt
 * @modifiedBy
 */
@RestController
@RequestMapping(CONTEXT_PATH + "/job-cards")
@AllArgsConstructor
@Slf4j
@Tag(name = "JobCard Endpoints", description = "This contains all endpoints that are used to interact with the jobCard model")
public class JobCardController {

    private final JobCardService jobCardService;

    /**
     * Handles a GET request to retrieve all jobCards.
     *
     * @return ResponseEntity containing the ResponseDTO with the list of jobCards paginated.
     */
    @GetMapping
    public ResponseEntity<ResponseDTO>getAllJobCards(@RequestParam(defaultValue = "{}") Map<String, String> params) {
        return jobCardService.findAllJobCards(params);
    }

    /**
     * Handles a GET request to retrieve an jobCard by its ID.
     *
     * @param id The ID of the jobCard to retrieve.
     * @return ResponseEntity containing the ResponseDTO with the requested jobCard.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getJobCardById(@PathVariable UUID id){
        return jobCardService.findById(id);
    }

    /**
     * Handles a POST request to create a new jobCard.
     *
     * @param jobCardDTO The JobCardDTO containing information of the new jobCard.
     * @return ResponseEntity containing the ResponseDTO with the created jobCard.
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> saveJobCard(@Valid @RequestBody JobCardDTO jobCardDTO){
        return jobCardService.saveAndPublish(jobCardDTO);
    }

    /**
     * Handles a PUT request to update an existing jobCard.
     *
     * @param jobCardDTO The updated JobCardDTO containing information to update jobCard.
     * @param id The ID of the jobCard to be updated.
     * @return ResponseEntity containing the ResponseDTO with the updated jobCard.
     */
    @PutMapping({"/{id}"})
    public ResponseEntity<ResponseDTO> updateJobCard(@PathVariable(name = "id") UUID id,@RequestBody JobCardDTO jobCardDTO) {
        return jobCardService.update(id, jobCardDTO);
    }

}
