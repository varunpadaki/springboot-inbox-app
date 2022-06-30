package com.springboot.inbox.service;

import com.springboot.inbox.domain.EmailDto;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.InboxAppException;
import com.springboot.inbox.exception.RecordNotFoundException;

public interface EmailService {
    EmailDto fetchUserEmail(String id,String username,String label) throws BadRequestException, RecordNotFoundException;

    EmailDto sendEmail(EmailDto emailDto) throws InboxAppException, BadRequestException;
}
