package com.springboot.inbox.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "folders_by_users")
@Getter
@Setter
public class Folder {

    @PrimaryKeyColumn(name = "user_id",ordinal = 0,type= PrimaryKeyType.PARTITIONED)
    private String id;

    @PrimaryKeyColumn(name = "label",ordinal = 1,type=PrimaryKeyType.CLUSTERED,ordering = Ordering.ASCENDING)
    @CassandraType(type=CassandraType.Name.TEXT)
    private String label;

    @CassandraType(type=CassandraType.Name.TEXT)
    private String color;
}
