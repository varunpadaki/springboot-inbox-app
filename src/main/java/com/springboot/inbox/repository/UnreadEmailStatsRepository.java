package com.springboot.inbox.repository;

import com.springboot.inbox.domain.UnreadEmailStats;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnreadEmailStatsRepository extends CassandraRepository<UnreadEmailStats,String> {

    List<UnreadEmailStats> findAllById(String id);

    @Query("update unread_email_stats set unread_count = unread_count+1 where user_id=?0 and label=?1")
    void incrementUnreadCounter(String id,String label);

    @Query("update unread_email_stats set unread_count = unread_count-1 where user_id=?0 and label=?1")
    void decrementUnreadCounter(String id,String label);
}
