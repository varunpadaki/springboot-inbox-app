package com.springboot.inbox.repository;

import com.springboot.inbox.domain.EmailList;
import com.springboot.inbox.domain.EmailListPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailListRepository extends CassandraRepository<EmailList, EmailListPrimaryKey> {

    @Query("select * from emails_by_user where user_id=?0 and label=?1")
    List<EmailList> findEmailListByUseridAndLabel(String userId,String label);
}
