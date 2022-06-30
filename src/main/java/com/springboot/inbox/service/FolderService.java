package com.springboot.inbox.service;

import com.springboot.inbox.domain.Folder;
import com.springboot.inbox.exception.BadRequestException;
import com.springboot.inbox.exception.RecordNotFoundException;

import java.util.List;

public interface FolderService {
    List<Folder> getFoldersByUsers(String userId) throws RecordNotFoundException, BadRequestException;
}
