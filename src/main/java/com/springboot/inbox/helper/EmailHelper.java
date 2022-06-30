package com.springboot.inbox.helper;

import com.springboot.inbox.domain.EmailDto;
import com.springboot.inbox.domain.EmailListDto;
import com.springboot.inbox.domain.EmailListPrimaryKey;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.service.EmailListService;
import com.springboot.inbox.service.UnreadEmailStatsService;
import com.springboot.inbox.utils.InboxAppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailHelper {

    private Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    @Autowired
    private EmailListService emailListServiceImpl;

    @Autowired
    private UnreadEmailStatsService unreadEmailStatsServiceImpl;

    public void processSendEmailSuccessResponse(EmailDto emailDto) {
        //store email in email list table with same time UUID for each toUser
        //Also store the email details in SentItems for the user with same timeUuid

        this.updateEmailListForToUsers(emailDto);
        this.updateEmailListForFromUser(emailDto);
    }

    private void updateEmailListForToUsers(EmailDto emailDto) {
        emailDto.getTo().stream().forEach(toUserId -> {
            EmailListPrimaryKey emailListPrimaryKey = new EmailListPrimaryKey();
            emailListPrimaryKey.setLabel("Inbox");
            emailListPrimaryKey.setUserId(toUserId);
            emailListPrimaryKey.setTimeId(emailDto.getId());

            EmailListDto emailListDto = new EmailListDto();
            emailListDto.setId(emailListPrimaryKey);
            emailListDto.setFrom(emailDto.getFrom());
            emailListDto.setRead(Boolean.FALSE);
            emailListDto.setTo(emailDto.getTo());
            emailListDto.setSubject(emailDto.getSubject());
            try {
                emailListServiceImpl.addEmailByUserAndFolder(emailListDto);
                unreadEmailStatsServiceImpl.incrementUnreadCountByFolder(toUserId,"Inbox");
            } catch (InboxAppException e) {
                logger.error(e.getErrorMessage());
            } catch (BadRequestException e) {
                logger.error(e.getErrorMessage());
            }
        });
    }

    private void updateEmailListForFromUser(EmailDto emailDto){
        EmailListPrimaryKey emailListPrimaryKey = new EmailListPrimaryKey();
        emailListPrimaryKey.setLabel("Sent");
        emailListPrimaryKey.setUserId(emailDto.getFrom());
        emailListPrimaryKey.setTimeId(emailDto.getId());

        EmailListDto emailListDto = new EmailListDto();
        emailListDto.setId(emailListPrimaryKey);
        emailListDto.setFrom(emailDto.getFrom());
        emailListDto.setRead(Boolean.TRUE);
        emailListDto.setTo(emailDto.getTo());
        emailListDto.setSubject(emailDto.getSubject());
        try {
            emailListServiceImpl.addEmailByUserAndFolder(emailListDto);
        } catch (InboxAppException e) {
            logger.error(e.getErrorMessage());
        } catch (BadRequestException e) {
            logger.error(e.getErrorMessage());
        }
    }

    public void processFetchUserEmailSuccessResponse(EmailDto emailDto,String userId,String label) {
        //get email list and set isRead = true and also decrement UnreadCounter
        try {
            if(InboxAppConstants.SENT_LABEL.equalsIgnoreCase(label)){
                return;
            }
            EmailListDto emailListDto = emailListServiceImpl.fetchEmailById(userId,label,emailDto.getId());
            if(emailListDto.isRead()){
                return;
            }
            emailListDto.setRead(Boolean.TRUE);
            emailListServiceImpl.updateEmailById(emailListDto);
            this.updateUnreadCounter(userId,label);
        } catch (RecordNotFoundException e) {
            logger.error(e.getErrorMessage());
            return;
        } catch (BadRequestException e) {
            logger.error(e.getErrorMessage());
            return;
        } catch (InboxAppException e) {
            logger.error(e.getErrorMessage());
        }
    }

    private void updateUnreadCounter(String userId,String label) throws BadRequestException {
        unreadEmailStatsServiceImpl.decrementUnreadCountByFolder(userId,label);
    }
}
