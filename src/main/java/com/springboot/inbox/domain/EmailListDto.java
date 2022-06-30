package com.springboot.inbox.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailListDto {
    private EmailListPrimaryKey id;
    private String from;
    private List<String> to;
    private String subject;
    private boolean isRead;
    private String agoTimeString;
}
