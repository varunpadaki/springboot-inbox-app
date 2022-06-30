package com.springboot.inbox.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.springboot.inbox.domain.Email;
import com.springboot.inbox.domain.EmailDto;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.helper.EmailHelper;
import com.springboot.inbox.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService{

    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailHelper emailHelper;

    @Override
    public EmailDto fetchUserEmail(String id,String userId,String label) throws BadRequestException, RecordNotFoundException {
        if(!StringUtils.hasLength(id)){
            String errorMessage = "UUID is empty/null cannot fetch email details";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        UUID uuid = UUID.fromString(id);
        Optional<Email> emailObj = emailRepository.findById(uuid);
        if(emailObj.isEmpty()){
            String errorMessage = String.format("Email not found with UUID : %s",uuid);
            logger.error(errorMessage);
            throw new RecordNotFoundException(errorMessage,errorMessage, HttpStatus.NOT_FOUND.toString());
        }
        logger.info("Email found successfully for id : {}",id);
        EmailDto emailDto = new EmailDto();
        BeanUtils.copyProperties(emailObj.get(),emailDto);
        emailHelper.processFetchUserEmailSuccessResponse(emailDto,userId,label);
        return emailDto;
    }

    @Override
    public EmailDto sendEmail(EmailDto emailDto) throws InboxAppException, BadRequestException {
        if(ObjectUtils.isEmpty(emailDto)){
            String errorMessage = "Email object is empty or null cannot send email.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage,errorMessage,HttpStatus.BAD_REQUEST.toString());
        }

        UUID timeUuid = Uuids.timeBased();
        emailDto.setId(timeUuid);
        Email email = new Email();
        BeanUtils.copyProperties(emailDto,email);
        Email savedEmail = emailRepository.insert(email);
        if(savedEmail == null){
            String errorMessage = String.format("Failed to save email for user : %s",email.getFrom());
            logger.error(errorMessage);
            throw new InboxAppException(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR,errorMessage,HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        emailHelper.processSendEmailSuccessResponse(emailDto);
        return emailDto;
    }
}
