package com.example.Centrifugo.setup.service;


import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.SetupDTO;
import com.example.Centrifugo.setup.model.LOV;
import com.example.Centrifugo.setup.model.SetupModel;
import com.example.Centrifugo.setup.repository.LOVRepository;
import com.example.Centrifugo.setup.repository.SetupRepository;
import com.example.Centrifugo.utility.ObjectNotValidException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.Centrifugo.utility.AppUtils.*;

@Service
@AllArgsConstructor
@Slf4j
public class SetupServiceImpl implements SetupService{

    private final SetupRepository setupRepository;
    private final LOVRepository lovRepository;


    @Override
    public ResponseEntity<ResponseDTO> findAllSetup(Map<String, String> params) {
        log.info("Inside find All Setups :::: Trying to fetch setups per given params");

        ResponseDTO response;
        try {
            List<SetupModel> setupModels;
            setupModels = setupRepository.findAll();
            if (!setupModels.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, setupModels);
                setupModels.stream()
                        .map(item -> {
                            SetupDTO setupDTO = new SetupDTO();
                            setupDTO.setId(item.getId());
                            setupDTO.setName(item.getName());
                            setupDTO.setCategoryId(item.getCategoryId());
                            return  setupDTO;
                        })
                        .collect(Collectors.toList());
                response = getResponseDTO("Successfully retrieved all setups", HttpStatus.OK, setupModels);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }else {
                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 204, HttpStatus.NO_CONTENT, "Record Not Found");
                response = getResponseDTO("No record found", HttpStatus.NO_CONTENT);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
        } catch (ResponseStatusException e) {
            log.error("Exception Occured! and Message -> {} and Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
            return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! StatusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> findById(UUID id) {
        log.info("Inside find Find by Setup ID Method ::: Trying to find setup by the  id -> {}", id);
        ResponseDTO response;
        try {
            var res = setupRepository.findById(id);
            if (res.isPresent()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, res);
                response = getResponseDTO("Successfully retrieved the setup with the id " + id, HttpStatus.OK, res);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
            log.info("No record found! statusCode -> {} and Message -> {}", HttpStatus.NOT_FOUND, res);
            response = getResponseDTO("Not Found!", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

        }  catch (ResponseStatusException e) {
            log.error("Exception Occurred! Reason -> {} and Message -> {}", e.getCause(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    /**
     * This method handles the creation of a new setup entity.
     *
     * Constructs a `SetupModel` object with details from setupDto`and the current user and timestamp.
     * Saves the `SetupModel` object to the database.
     * If the save operation is successful and returns a valid ID:
     * Creates a new `LOV` object with details derived from the saved `SetupModel`.
     * Saves the `LOV` object to the database.
     *
     * @param setupDto The data transfer object containing setup details.
     * @return A response entity containing the result of the operation.
     */
    @Override
    public ResponseEntity<ResponseDTO> createSetup(SetupDTO setupDto) {
        log.info("Inside the create Setup method :::: Trying to Save Setup");
        ResponseDTO response;
        try {
            var res = SetupModel.builder()
                    .name(setupDto.getName())
                    .categoryId(setupDto.getCategoryId())
                    .isEnabled(true)
                    .updatedBy(getAuthenticatedUserId())
                    .updatedAt(ZonedDateTime.now())
                    .createdBy(getAuthenticatedUserId())
                    .createdAt(ZonedDateTime.now())
                    .build();
            var record = setupRepository.save(res);
            if (isNotNullOrEmpty(record.getId())) {
                LOV setupLOV = new LOV();
                setupLOV.setCategoryId(record.getCategoryId());
                setupLOV.setSetupId(record.getId());
                setupLOV.setIsEnabled(true);
                setupLOV.setCreatedBy(getAuthenticatedUserId());
                setupLOV.setCreatedAt(ZonedDateTime.now());
                setupLOV.setUpdatedBy(getAuthenticatedUserId());
                setupLOV.setUpdatedAt(ZonedDateTime.now());
                lovRepository.save(setupLOV);
            }
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.CREATED, record);
            response = getResponseDTO("Record saved successfully", HttpStatus.OK, record);
        } catch (ResponseStatusException e) {
            log.error("Error Occurred! statusCode -> {}, Message -> {}, Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occurred! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);

        } catch (DataIntegrityViolationException e) {
            log.error("Exception Occurred! Message -> {} and Cause -> {}", e.getMostSpecificCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    /**
     * This method handles the update of an existing setup entity.
     *
     * Attempts to find an existing `SetupModel` entity by its ID:
     *    - If not found, throws a `ResponseStatusException` with a 404 Not Found status.
     * Updates the properties of the found `SetupModel` entity using the data from `setupDto`.
     * Saves the updated `SetupModel` back to the database.
     * If the save operation is successful and returns a valid ID:
     * - Creates a new `LOV` object.
     * - Attempts to find an existing `LOV` record using the setup ID:
     * - If found, updates the `LOV` record's properties and saves it.
     * - If not found, throws a `ResponseStatusException` with a 404 Not Found status.
     *
     * @param id The ID of the setup entity to be updated.
     * @param setupDto The data transfer object containing the updated setup details.
     * @return A response entity containing the result of the operation.
     */
    @Override
    public ResponseEntity<ResponseDTO> updateSetup(UUID id, SetupDTO setupDto) {
        log.info("Inside Update Setup Method :::: Trying to update A Setup -> {}", id);
        ResponseDTO response;

        try{
            SetupModel existingSetup = setupRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "setup with Id " + id + "Does Not Exist"));
            existingSetup.setName(setupDto.getName());
            existingSetup.setCategoryId(setupDto.getCategoryId());

            var record = setupRepository.save(existingSetup);

            if (isNotNullOrEmpty(record.getId())) {
                LOV setupLOV = new LOV();
               var existingRes =  lovRepository.findBySetupId(setupLOV.getSetupId());
               if (isNotNullOrEmpty(existingRes)) {
                   existingRes.setCategoryId(record.getCategoryId());
                   existingRes.setUpdatedBy(getAuthenticatedUserId());
                   existingRes.setUpdatedAt(ZonedDateTime.now());
               }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "setup with Id " + id + "Does Not Exist");
            }
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.ACCEPTED, record);
            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, record);
        } catch (ResponseStatusException e) {
            log.error("Exception Occurred! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occurred! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
//
//    if (isNotNullOrEmpty(record.getId())) {
//        // Assuming `record` has a valid setupId
//        UUID setupId = record.getSetupId();  // Assuming `getSetupId()` exists in `record`
//        var existingRes = lovRepository.findBySetupId(setupId);
//
//        if (existingRes != null) {
//            existingRes.setId(record.getId());  // Set the new Id from `record`
//            return existingRes;  // Return the updated entity
//        } else {
//            // Handle the case where no entity is found
//            throw new EntityNotFoundException("No LOV found for the provided setupId");
//        }
//    }


    @Override
    public ResponseEntity<ResponseDTO> disableSetup(UUID id, SetupDTO setupDto) {
        log.info("Inside Disable value Method ::: Trying To Delete value Per Given Params");
        ResponseDTO response;
        try {
            var existingSetup = setupRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setup " + "does not exist"));
            existingSetup.setIsEnabled(setupDto.isEnabled());

            var record = setupRepository.save(existingSetup);
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, existingSetup.getId() + " status set to " + setupDto.isEnabled());
            response = getResponseDTO("Value updated to " + existingSetup.getIsEnabled(), HttpStatus.ACCEPTED, record);
        } catch (ResponseStatusException e) {
            log.error("Exception Occurred!, Message -> {}, Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception occurred! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

}
