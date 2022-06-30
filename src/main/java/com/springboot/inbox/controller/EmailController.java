package com.springboot.inbox.controller;

import com.springboot.inbox.domain.EmailDto;
import com.springboot.inbox.domain.ErrorResponse;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping(value="/inbox")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailController {

    //store each new email with UUID(time uuid) as primary key,
    //also store email in email list table with same time UUID for each toUser
    //and when each toUser login display the emailist,
    //use same UUID while fetching email details from List VIEW for each toUser
    //Also store the email details in SentItems for the user with same timeUuid

    private Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailServiceImpl;

    @GetMapping("/emails/{id}")
    public ResponseEntity<Object> fetchUserEmail(@PathVariable(name = "id")String id,@RequestParam(name = "userId")String userId,@RequestParam(name = "label")String label){
        try {
            EmailDto emailDto = emailServiceImpl.fetchUserEmail(id, userId, label);
            return new ResponseEntity<>(emailDto, HttpStatus.OK);
        } catch (BadRequestException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        } catch (RecordNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            String errorMessage = String.format("Error occurred in EmailController fetchUserEmail for %s", id);
            logger.error(errorMessage, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/emails")
    public ResponseEntity<Object> sendEmail(@RequestBody EmailDto emailDto) {
        try {
            EmailDto savedEmail = emailServiceImpl.sendEmail(emailDto);
            return new ResponseEntity<>(savedEmail,HttpStatus.CREATED);
        } catch (InboxAppException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, e.getHttpStatus());
        } catch (BadRequestException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            String errorMessage = String.format("Error occurred in EmailController sendEmail for %s", emailDto.getFrom());
            logger.error(errorMessage, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}