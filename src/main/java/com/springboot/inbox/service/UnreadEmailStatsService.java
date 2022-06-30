package com.springboot.inbox.service;

import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.RecordNotFoundException;

import java.util.Map;

public interface UnreadEmailStatsService {
    Map<String, Integer> getUnreadCountByFolder(String userId) throws BadRequestException, RecordNotFoundException;

    void incrementUnreadCountByFolder(String userId, String label) throws BadRequestException;

    void decrementUnreadCountByFolder(String userId, String label) throws BadRequestException;
}
