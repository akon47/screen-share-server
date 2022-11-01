package com.hwans.screenshareserver.mapper;

import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateMessageRequestDto;
import com.hwans.screenshareserver.dto.sharing.SimpleMessageDto;
import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import com.hwans.screenshareserver.entity.sharing.SharingMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class SharingMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(target = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    public abstract SharingChannel toEntity(CreateChannelRequestDto createChannelRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    public abstract SharingMessage toEntity(CreateMessageRequestDto createMessageRequestDto);

    public abstract SimpleMessageDto entityToSimpleMessageDto(SharingMessage sharingMessage);

    @Named("encodePassword")
    String encoderPassword(String password) {
        if (password == null)
            return null;

        return passwordEncoder.encode(password);
    }
}
