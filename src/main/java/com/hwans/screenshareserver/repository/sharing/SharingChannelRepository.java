package com.hwans.screenshareserver.repository.sharing;

import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SharingChannelRepository extends JpaRepository<SharingChannel, UUID> {
    @Query("select c from SharingChannel c where c.publicChannel = true order by c.createdAt desc")
    List<SharingChannel> findPublicChannels();

    @Query("select c from SharingChannel c where c.createdAt < :threshold")
    List<SharingChannel> findCreatedBefore(@Param("threshold") LocalDateTime threshold);
}
