package com.springboot.inbox.service;

import com.springboot.inbox.domain.Folder;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.repository.FolderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    private Logger logger = LoggerFactory.getLogger(FolderServiceImpl.class);

    @Autowired
    private FolderRepository folderRepository;

    @Override
    public List<Folder> getFoldersByUsers(String userId) throws BadRequestException,RecordNotFoundException {
        if(!StringUtils.hasLength(userId)){
            String errorMessage = "User ID is empty/null cannot fetch folders.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage, errorMessage, HttpStatus.BAD_REQUEST.toString());
        }
        List<Folder> userFolders = folderRepository.findAllById(userId);
        if(CollectionUtils.isEmpty(userFolders)){
            String errorMessage = String.format("Folders not found for user : %s",userId);
            logger.error(errorMessage);
            throw new RecordNotFoundException(errorMessage, errorMessage, HttpStatus.NOT_FOUND.toString());
        }
        logger.info("Folders found for user : {}",userId);
        return userFolders;
    }
}