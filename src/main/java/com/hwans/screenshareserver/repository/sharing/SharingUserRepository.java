package com.hwans.screenshareserver.repository.sharing;

import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import com.hwans.screenshareserver.entity.sharing.SharingUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SharingUserRepository extends JpaRepository<SharingUser, UUID> {
    @Modifying
    @Query("delete from SharingUser u where u.channel.id = :channelId")
    void deleteAllByChannelId(@Param("channelId") UUID channelId);
}
