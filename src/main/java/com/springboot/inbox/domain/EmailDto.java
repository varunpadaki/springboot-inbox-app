package com.springboot.inbox.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EmailDto {

    private UUID id;
    private String from;
    private List<String> to;
    private String subject;
    private String body;
}
