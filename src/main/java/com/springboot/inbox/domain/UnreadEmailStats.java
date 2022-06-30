package com.springboot.inbox.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "unread_email_stats")
@Getter
@Setter
public class UnreadEmailStats {

    @PrimaryKeyColumn(name = "user_id",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String id;

    @PrimaryKeyColumn(name = "label",ordinal = 1,type=PrimaryKeyType.CLUSTERED)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String label;

    @Column("unread_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    private int unreadCount;
}
