package com.springboot.inbox.controller;

import com.springboot.inbox.domain.ErrorResponse;
import com.springboot.inbox.domain.Folder;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.service.FolderService;
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
public class FolderController {

    //get email list based on userid and folder

    //In cassandra data is distributed across multiple nodes and multiple tables
    //since there is no join in cassandra there will be multiple API calls and DB calls
    //to get all the information.

    //Use one generic controller and autowire one generic service and use multiple repository
    //within service based on requirement

    private Logger logger = LoggerFactory.getLogger(FolderController.class);

    @Autowired
    private FolderService folderServiceImpl;

    @GetMapping("/folders/{userId}")
    public ResponseEntity<Object> getFoldersByUser(@PathVariable(name = "userId") String userId){
        try{
            List<Folder> folders = folderServiceImpl.getFoldersByUsers(userId);
            return new ResponseEntity<>(folders,HttpStatus.OK);
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
            String errorMessage = String.format("Error occurred in FolderController getFoldersByUsers for %s", userId);
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
