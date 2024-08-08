package com.example.Centrifugo.setup.service;


import com.example.Centrifugo.dto.LOVCategoryDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.setup.model.LOVCategory;
import com.example.Centrifugo.setup.repository.LOVCategoryRepository;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.Centrifugo.utility.AppUtils.getAuthenticatedUserId;
import static com.example.Centrifugo.utility.AppUtils.getResponseDTO;

@Service
@AllArgsConstructor
@Slf4j
public class LOVCategoryServiceImpl implements LOVCategoryService{

    private final LOVCategoryRepository lovCategoryRepository;

    @Override
    public ResponseEntity<ResponseDTO> findAllCategories(Map<String, String> params) {
        log.info("Inside find All Categories :::: Trying to fetch Categories per given params");
        ResponseDTO response;
        try {
            List<LOVCategory> lovCategories;
            lovCategories = lovCategoryRepository.findAll();
            if (!lovCategories.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, lovCategories);
                lovCategories.stream()
                        .map(lovCategory -> {
                            LOVCategoryDTO lovCategoryDTO = new LOVCategoryDTO();
                            lovCategoryDTO.setId(lovCategory.getId());
                            lovCategoryDTO.setName(lovCategory.getName());
                            lovCategoryDTO.setEnabled(lovCategory.getIsEnabled());
                            lovCategoryDTO.setCreatedBy(getAuthenticatedUserId());
                            lovCategoryDTO.setCreatedAt(ZonedDateTime.now());
                            lovCategoryDTO.setUpdatedBy(getAuthenticatedUserId());
                            lovCategoryDTO.setUpdatedAt(ZonedDateTime.now());
                            return lovCategoryDTO;
                        })
                        .collect(Collectors.toList());

                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 204, HttpStatus.NO_CONTENT, "Record Not Found");
                response = getResponseDTO("No record found", HttpStatus.NO_CONTENT);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }else {
                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 404, HttpStatus.NOT_FOUND, "Record Not Found");
                response = getResponseDTO("No record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
        } catch (ResponseStatusException e) {
            log.error("Exception Occurred! and Message -> {} and Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
            return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occurred! StatusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method finds the category by their respective id
     * @param id represents the ID of the id we are finding
     * @return returns the response and the status code
     */
    @Override
    public ResponseEntity<ResponseDTO> findById(UUID id) {
        log.info("Inside find Find by CategoryId Method::: Trying to find categories by the id -> {}", id);
        ResponseDTO response;
        try {
            var res = lovCategoryRepository.findById(id);
            if (res.isPresent()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, res);
                response = getResponseDTO("Successfully retrieved the category with the id " + id, HttpStatus.OK, res);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
            log.info("No record found! statusCode -> {} and Message -> {}", HttpStatus.NOT_FOUND, res);
            response = getResponseDTO("Not Found!", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

        }  catch (ResponseStatusException e) {
            log.error("Exception Occured! Reason -> {} and Message -> {}", e.getCause(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }


    /**
     * This method saves the categories in the database
     * @param lovCategoryDTO represents the object to be saved
     * @return returns the response and the status code
     */
    @Override
    public ResponseEntity<ResponseDTO> createCategory(LOVCategoryDTO lovCategoryDTO) {
        log.info("Inside the create LOV Category Method :::: Trying to Save LOV Category");
        ResponseDTO response;
        try{
            var res = LOVCategory.builder()
                    .name(lovCategoryDTO.getName())
                    .isEnabled(lovCategoryDTO.isEnabled())
                    .createdBy(getAuthenticatedUserId())
                    .createdAt(ZonedDateTime.now())
                    .updatedBy(getAuthenticatedUserId())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            var record = lovCategoryRepository.save(res);
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

    @Override
    public ResponseEntity<ResponseDTO> updateCategory(UUID id, LOVCategoryDTO lovCategoryDTO) {
        log.info("Inside Update Category Method :::: Trying to update A Category -> {}", id);
        ResponseDTO response;

        try {
            LOVCategory existingCategory = lovCategoryRepository.findById(id)
                    .orElseThrow(()
                            -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category with Id " + id + "Does Not Exist"));
            existingCategory.setName(lovCategoryDTO.getName());
            existingCategory.setUpdatedBy(getAuthenticatedUserId());
            existingCategory.setUpdatedAt(ZonedDateTime.now());

            var record = lovCategoryRepository.save(existingCategory);
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.ACCEPTED, record);
            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, record);
        }catch (ResponseStatusException e) {
            log.error("Exception Occured! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occured! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @Override
    public ResponseEntity<ResponseDTO> disableCategory(UUID id, LOVCategoryDTO lovCategoryDTO) {
        log.info("Inside Disable Category Method ::: Trying To Delete value Per Given Params");
        ResponseDTO response;

        try{
            var existingCategory = lovCategoryRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Record " + "does not exist"));
            existingCategory.setIsEnabled(lovCategoryDTO.isEnabled());
            existingCategory.setUpdatedBy(getAuthenticatedUserId());
            existingCategory.setUpdatedAt(ZonedDateTime.now());

            var record = lovCategoryRepository.save(existingCategory);
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, existingCategory.getName() + " status set to " + lovCategoryDTO.isEnabled());
            response = getResponseDTO("Category updated to " + existingCategory.getIsEnabled(), HttpStatus.ACCEPTED, record);
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
