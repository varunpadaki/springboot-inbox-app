package com.springboot.inbox.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.springboot.inbox.domain.Email;
import com.springboot.inbox.domain.EmailList;
import com.springboot.inbox.domain.EmailListDto;
import com.springboot.inbox.domain.EmailListPrimaryKey;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.repository.EmailListRepository;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EmailListServiceImpl implements EmailListService{

    private Logger logger = LoggerFactory.getLogger(EmailListServiceImpl.class);

    @Autowired
    private EmailListRepository emailListRepository;

    private PrettyTime prettyTime = new PrettyTime();

    @Override
    public List<EmailListDto> fetchEmailByUserAndLabel(String userId, String label) throws BadRequestException, RecordNotFoundException {
        if(!StringUtils.hasLength(userId)){
            String errorMessage = "User ID is empty/null cannot fetch email list";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        if(!StringUtils.hasLength(label)){
            String errorMessage = "Folder label is empty/null cannot fetch email list";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage,HttpStatus.BAD_REQUEST.toString());
        }

        List<EmailList> userEmails = emailListRepository.findEmailListByUseridAndLabel(userId,label);
        if(CollectionUtils.isEmpty(userEmails)){
            String errorMessage = String.join("Emails not found for user %s and label %s",userId,label);
            logger.error(errorMessage);
            throw new RecordNotFoundException(errorMessage,errorMessage,HttpStatus.NOT_FOUND.toString());
        }
        logger.info("Emails found for users : {}.",userId);
        List<EmailListDto> emailDtoList = new ArrayList<>();
        userEmails.stream().forEach(userEmail -> {
            EmailListDto userEmailDto = new EmailListDto();
            BeanUtils.copyProperties(userEmail,userEmailDto);
            Date emailDate = new Date(Uuids.unixTimestamp(userEmail.getId().getTimeId()));
            userEmailDto.setAgoTimeString(prettyTime.format(emailDate));
            emailDtoList.add(userEmailDto);
        });
        return emailDtoList;
    }

    @Override
    public EmailListDto addEmailByUserAndFolder(EmailListDto emailListDto) throws InboxAppException, BadRequestException {
        if(ObjectUtils.isEmpty(emailListDto)){
            String errorMessage = "EmailList object is empty/null cannot add email to null";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage,HttpStatus.BAD_REQUEST.toString());
        }
        EmailList emailList = new EmailList();
        BeanUtils.copyProperties(emailListDto,emailList);
        EmailList savedEmailEntry = emailListRepository.insert(emailList);
        if(savedEmailEntry == null){
            String errorMessage = String.format("Failed to add email to list for user : %s",emailListDto.getId().getUserId());
            logger.error(errorMessage);
            throw new InboxAppException(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR,errorMessage,HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return emailListDto;
    }

    @Override
    public EmailListDto fetchEmailById(String userId, String folder, UUID id) throws RecordNotFoundException, BadRequestException {
        if(!StringUtils.hasLength(userId)){
            String errorMessage = "User ID is empty/null cannot fetch email by id";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        if(!StringUtils.hasLength(folder)){
            String errorMessage = "Folder is empty/null cannot fetch email by id";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        if(id == null){
            String errorMessage = "UUID is empty/null cannot fetch email by id";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage, HttpStatus.BAD_REQUEST.toString());
        }
        EmailListPrimaryKey key = new EmailListPrimaryKey();
        key.setTimeId(id);
        key.setLabel(folder);
        key.setUserId(userId);
        Optional<EmailList> emailObj = emailListRepository.findById(key);
        if(emailObj.isEmpty()){
            String errorMessage = String.format("Email not found by ID for user : %s and timeUuid : %s",userId,id.toString());
            logger.error(errorMessage);
            throw new RecordNotFoundException(errorMessage,errorMessage,HttpStatus.NOT_FOUND.toString());
        }
        EmailListDto emailListDto = new EmailListDto();
        BeanUtils.copyProperties(emailObj.get(),emailListDto);
        return emailListDto;
    }

    @Override
    public EmailListDto updateEmailById(EmailListDto emailListDto) throws BadRequestException, InboxAppException {
        if(ObjectUtils.isEmpty(emailListDto)){
            String errorMessage = "EmailList object is empty/null cannot update email list.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage,HttpStatus.BAD_REQUEST.toString());
        }

        EmailList emailList = new EmailList();
        BeanUtils.copyProperties(emailListDto,emailList);
        EmailList savedEmailEntry = emailListRepository.save(emailList);
        if(savedEmailEntry == null){
            String errorMessage = String.format("Failed to update email to list for user : %s",emailListDto.getId().getUserId());
            logger.error(errorMessage);
            throw new InboxAppException(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR,errorMessage,HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return emailListDto;
    }
}