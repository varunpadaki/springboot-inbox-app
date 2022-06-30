package com.springboot.inbox.service;

import com.springboot.inbox.domain.EmailListDto;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;

import java.util.List;
import java.util.UUID;

public interface EmailListService {
    List<EmailListDto> fetchEmailByUserAndLabel(String userId, String label) throws BadRequestException, RecordNotFoundException;

    EmailListDto addEmailByUserAndFolder(EmailListDto emailListDto) throws InboxAppException, BadRequestException;

    EmailListDto fetchEmailById(String userId, String folder, UUID id) throws RecordNotFoundException, BadRequestException;

    EmailListDto updateEmailById(EmailListDto emailListDto) throws BadRequestException, InboxAppException;
}
