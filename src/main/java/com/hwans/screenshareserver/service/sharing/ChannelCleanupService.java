package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import com.hwans.screenshareserver.repository.sharing.SharingChannelRepository;
import com.hwans.screenshareserver.repository.sharing.SharingMessageRepository;
import com.hwans.screenshareserver.repository.sharing.SharingUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Periodically removes stale sharing channels (and their users/messages) that
 * are no longer active, so that abandoned rooms do not linger in the database
 * or in the public channel list.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelCleanupService {
    // A channel must be at least this old before it is eligible for cleanup,
    // which avoids deleting freshly created channels whose host has not yet
    // established a WebSocket connection.
    private static final Duration MIN_AGE_BEFORE_CLEANUP = Duration.ofMinutes(10);

    private final SharingChannelRepository sharingChannelRepository;
    private final SharingUserRepository sharingUserRepository;
    private final SharingMessageRepository sharingMessageRepository;
    private final SharingWebSocketHandler sharingWebSocketHandler;

    @Scheduled(fixedDelayString = "300000", initialDelayString = "300000")
    @Transactional
    public void cleanupStaleChannels() {
        var threshold = LocalDateTime.now().minus(MIN_AGE_BEFORE_CLEANUP);
        Set<UUID> activeChannelIds = sharingWebSocketHandler.getActiveChannelIds();

        var staleChannels = sharingChannelRepository.findCreatedBefore(threshold).stream()
                .filter(channel -> !activeChannelIds.contains(channel.getId()))
                .toList();

        for (SharingChannel channel : staleChannels) {
            deleteChannel(channel);
        }

        if (!staleChannels.isEmpty()) {
            log.info("Cleaned up {} stale sharing channel(s).", staleChannels.size());
        }
    }

    private void deleteChannel(SharingChannel channel) {
        var channelId = channel.getId();
        // Order matters because of the channel<->user foreign keys:
        // 1) remove messages, 2) detach the host reference, 3) remove users, 4) remove the channel.
        sharingMessageRepository.deleteAllByChannelId(channelId);
        channel.setHost(null);
        sharingChannelRepository.saveAndFlush(channel);
        sharingUserRepository.deleteAllByChannelId(channelId);
        sharingChannelRepository.delete(channel);
    }
}
