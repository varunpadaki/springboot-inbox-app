package com.springboot.inbox.service;

import com.springboot.inbox.domain.UnreadEmailStats;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.RecordNotFoundException;
import com.springboot.inbox.repository.UnreadEmailStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UnreadEmailStatsServiceImpl implements UnreadEmailStatsService {

    private Logger logger = LoggerFactory.getLogger(UnreadEmailStatsServiceImpl.class);

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;
    @Override
    public Map<String, Integer> getUnreadCountByFolder(String userId) throws BadRequestException, RecordNotFoundException {
        if(!StringUtils.hasLength(userId)){
            String errorMessage = "User ID is empty/null cannot fetch unread count.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage, errorMessage, HttpStatus.BAD_REQUEST.toString());
        }
        List<UnreadEmailStats> unreadEmailStats = unreadEmailStatsRepository.findAllById(userId);

        if(CollectionUtils.isEmpty(unreadEmailStats)){
            String errorMessage = String.format("Unread Email Stats not found for user : %s",userId);
            logger.error(errorMessage);
            throw new RecordNotFoundException(errorMessage, errorMessage, HttpStatus.NOT_FOUND.toString());
        }
        logger.info("Unread email stats found for user : {}",userId);
        return unreadEmailStats.stream().collect(Collectors.toMap(UnreadEmailStats::getLabel,UnreadEmailStats::getUnreadCount));
    }

    @Override
    public void incrementUnreadCountByFolder(String userId, String label) throws BadRequestException {
        if(!StringUtils.hasLength(userId)){
            String errorMessage = "User ID is empty/null cannot increment unread count.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage, errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        if(!StringUtils.hasLength(label)){
            String errorMessage = "Label is empty/null cannot increment unread count.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage, errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        unreadEmailStatsRepository.incrementUnreadCounter(userId,label);
    }

    @Override
    public void decrementUnreadCountByFolder(String userId, String label) throws BadRequestException {
        if(!StringUtils.hasLength(userId)){
            String errorMessage = "User ID is empty/null cannot decrement unread count.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage, errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        if(!StringUtils.hasLength(label)){
            String errorMessage = "Label is empty/null cannot decrement unread count.";
            logger.error(errorMessage);
            throw new BadRequestException(errorMessage, errorMessage, HttpStatus.BAD_REQUEST.toString());
        }

        unreadEmailStatsRepository.decrementUnreadCounter(userId,label);
    }
}
