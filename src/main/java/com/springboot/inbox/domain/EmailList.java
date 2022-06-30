package com.springboot.inbox.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table(value = "emails_by_user")
@Getter
@Setter
public class EmailList {
    @PrimaryKey
    private EmailListPrimaryKey id;

    @CassandraType(type=CassandraType.Name.TEXT)
    private String from;

    @CassandraType(type=CassandraType.Name.LIST,typeArguments = CassandraType.Name.TEXT)
    private List<String> to;

    @CassandraType(type=CassandraType.Name.TEXT)
    private String subject;

    @CassandraType(type=CassandraType.Name.BOOLEAN)
    private boolean isRead;
}
