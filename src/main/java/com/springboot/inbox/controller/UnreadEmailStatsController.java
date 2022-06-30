package com.springboot.inbox.controller;

import com.springboot.inbox.domain.ErrorResponse;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.service.UnreadEmailStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping(value = "/inbox")
@CrossOrigin(origins = "http://localhost:3000")
public class UnreadEmailStatsController {

    private Logger logger = LoggerFactory.getLogger(UnreadEmailStatsController.class);

    @Autowired
    private UnreadEmailStatsService unreadEmailStatsServiceImpl;

    @GetMapping(value = "/stats/{userId}")
    public ResponseEntity<Object> getUnreadCountByFolder(@PathVariable(name = "userId") String userId){
        try{
            Map<String,Integer> unreadEmailStats = unreadEmailStatsServiceImpl.getUnreadCountByFolder(userId);
            return new ResponseEntity<>(unreadEmailStats, HttpStatus.OK);
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
            String errorMessage = String.format("Error occurred in UnreadEmailStatsController getUnreadCountByFolder for %s", userId);
            logger.error(errorMessage, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stats/{userId}/increment")
    public ResponseEntity<Object> incrementUnreadCountByFolder(@PathVariable(name = "userId") String userId, @RequestParam(name = "label") String label){
        try {
            unreadEmailStatsServiceImpl.incrementUnreadCountByFolder(userId,label);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            String errorMessage = String.format("Error occurred in UnreadEmailStatsController incrementUnreadCountByFolder for %s", userId);
            logger.error(errorMessage, e);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setDebugMessage(e.getMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stats/{userId}/decrement")
    public ResponseEntity<Object> decrementUnreadCountByFolder(@PathVariable(name = "userId") String userId, @RequestParam(name = "label") String label){
        try {
            unreadEmailStatsServiceImpl.decrementUnreadCountByFolder(userId,label);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            //set custom error code if defined else set httpstatus code
            errorResponse.setErrorCode(e.getErrorCode());
            errorResponse.setErrorMessage(e.getErrorMessage());
            errorResponse.setDebugMessage(e.getDebugMessage());
            errorResponse.setTimestamp(Instant.now().toEpochMilli());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            String errorMessage = String.format("Error occurred in UnreadEmailStatsController decrementUnreadCountByFolder for %s", userId);
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
