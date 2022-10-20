package com.hwans.screenshareserver.repository.sharing;

import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SharingChannelRepository extends JpaRepository<SharingChannel, UUID> {
}
