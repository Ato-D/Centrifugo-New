package com.example.Centrifugo.setup.service;


import com.example.Centrifugo.dto.LOVDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.setup.model.LOV;
import com.example.Centrifugo.setup.repository.LOVRepository;
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

import static com.example.Centrifugo.utility.AppUtils.*;

@Service
@AllArgsConstructor
@Slf4j
public class LOVServiceImpl implements LOVService{

    private final LOVRepository lovRepository;


    /**
     * This method is use to find all the values saved in the db
     * @param params the query parameters we are passing
     * @return the respose object and the status code
     */

    public ResponseEntity<ResponseDTO> findAllValues(Map<String, String> params) {
        log.info("Inside find All Values :::: Trying to fetch values per given params");

        ResponseDTO response;
        try {

            List<LOV> lovList;
            lovList = lovRepository.findAll();
            if (!lovList.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, lovList);
                lovList.stream()
                        .map(lov -> {
                            LOVDTO lovdto = new LOVDTO();
                            lovdto.setId(lov.getId());
                            lovdto.setName(lov.getName());
                            lovdto.setCategoryid(lov.getCategoryId());
                            lovdto.setEnabled(lov.getIsEnabled());
                            lovdto.setCreatedBy(getAuthenticatedUserId());
                            lovdto.setCreatedAt(ZonedDateTime.now());
                            lovdto.setUpdatedBy(getAuthenticatedUserId());
                            lovdto.setUpdatedAt(ZonedDateTime.now());
                            return  lovdto;
                        })
                        .collect(Collectors.toList());

                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 204, HttpStatus.NO_CONTENT, "Record Not Found");
                response = getResponseDTO("No record found", HttpStatus.NO_CONTENT);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            } else {
                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 404, HttpStatus.NOT_FOUND, "Record Not Found");
                response = getResponseDTO("No record found", HttpStatus.NOT_FOUND);
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

    /**
     * This method finds the Values by their respective category id
     * @param categoryId represents the ID of the category we are finding
     * @return returns the response and the status code
     */
    @Override
    public ResponseEntity<ResponseDTO> getValuesByCategoryId(UUID categoryId) {
        log.info("Inside find Find Values by CategoryId Method::: Trying to find values by the category id -> {}", categoryId);
        ResponseDTO response;
        try {
            var res = lovRepository.findById(categoryId);
            if (res.isPresent()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, res);
                response = getResponseDTO("Successfully retrieved the values with the category id " + categoryId, HttpStatus.OK, res);
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
     * This method saves the values in the database
     * @param lovdto represents the object to be saved
     * @return returns the response and the status code
     */
    @Override
    public ResponseEntity<ResponseDTO> createValue(LOVDTO lovdto) {
        log.info("Inside the create LOV method :::: Trying to Save LOV");
        ResponseDTO response;
        try {
            var res = LOV.builder()
                    .name(lovdto.getName())
                    .categoryId(lovdto.getCategoryid())
                    .isEnabled(lovdto.isEnabled())
                    .createdBy(getAuthenticatedUserId())
                    .createdAt(ZonedDateTime.now())
                    .updatedBy(getAuthenticatedUserId())
                    .updatedAt(ZonedDateTime.now())
                    .build();

            var record = lovRepository.save(res);
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
    public ResponseEntity<ResponseDTO> updateValue(UUID id, LOVDTO lovdto) {
        log.info("Inside Update Value Method :::: Trying to update A Value -> {}", id);

        ResponseDTO response;

        try{
            LOV existingValue = lovRepository.findById(id)
                    .orElseThrow(()
                            -> new ResponseStatusException(HttpStatus.NOT_FOUND, "value with Id " + id + "Does Not Exist"));
                    existingValue.setName(lovdto.getName());
                    existingValue.setCategoryId(lovdto.getCategoryid());

                    var record = lovRepository.save(existingValue);
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
    public ResponseEntity<ResponseDTO> disableValue(UUID id, LOVDTO lovdto) {
        log.info("Inside Disable value Method ::: Trying To Delete value Per Given Params");
        ResponseDTO response;
        try {
            var existingValue = lovRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Value " + "does not exist"));
            existingValue.setIsEnabled(lovdto.isEnabled());
            existingValue.setUpdatedBy(UUID.fromString(authentication().getName()));
            existingValue.setUpdatedAt(ZonedDateTime.now());

            var record = lovRepository.save(existingValue);
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, existingValue.getName() + " status set to " + lovdto.isEnabled());
            response = getResponseDTO("Value updated to " + existingValue.getIsEnabled(), HttpStatus.ACCEPTED, record);
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
