package com.springboot.inbox.controller;

import com.springboot.inbox.domain.EmailListDto;
import com.springboot.inbox.domain.ErrorResponse;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.service.EmailListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/inbox")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailListController {

    private Logger logger = LoggerFactory.getLogger(EmailListController.class);

    @Autowired
    private EmailListService emailListServiceImpl;

    @GetMapping("/messages/{userId}")
    public ResponseEntity<Object> fetchEmailByUserAndLabel(@PathVariable(name = "userId") String userId, @RequestParam(name = "label") String label){
        try{
            List<EmailListDto> emailList = emailListServiceImpl.fetchEmailByUserAndLabel(userId,label);
            return new ResponseEntity<>(emailList, HttpStatus.OK);
        }catch(BadRequestException e){
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }catch(RecordNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            String errorMessage = String.format("Error occurred in EmailListController fetchEmailByUserAndLabel for %s", userId);
            logger.error(errorMessage, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<Object> addEmailByUserAndFolder(@RequestBody EmailListDto emailListDto) {
        try {
            EmailListDto savedEmailEntry = emailListServiceImpl.addEmailByUserAndFolder(emailListDto);
            return new ResponseEntity<>(savedEmailEntry,HttpStatus.OK);
        } catch (BadRequestException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }catch(InboxAppException e){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, e.getHttpStatus());
        }catch(Exception e){
            String errorMessage = String.format("Error occurred in EmailListController fetchEmailByUserAndLabel for %s", emailListDto.getId().getUserId());
            logger.error(errorMessage, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
