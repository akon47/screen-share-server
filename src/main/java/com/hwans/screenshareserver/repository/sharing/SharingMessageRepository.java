package com.hwans.screenshareserver.repository.sharing;

import com.hwans.screenshareserver.entity.sharing.SharingMessage;
import com.hwans.screenshareserver.entity.sharing.SharingUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SharingMessageRepository extends JpaRepository<SharingMessage, UUID> {
    @Query("select x from SharingMessage as x where x.deleted = false and x.author.channel.id = :channelId order by x.createdAt desc, x.id desc")
    List<SharingMessage> findAllByOrderByIdDesc(@Param("channelId") UUID channelId, Pageable page);

    @Query("select x from SharingMessage as x where x.deleted = false and x.author.channel.id = :channelId and ((x.createdAt < :createdAt and x.id < :id) or (x.createdAt < :createdAt)) order by x.createdAt desc, x.id desc")
    List<SharingMessage> findByIdLessThanOrderByIdDesc(@Param("channelId") UUID channelId, @Param("uuid") UUID id, @Param("createdAt") LocalDateTime createdAt, Pageable page);
}
