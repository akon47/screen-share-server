package com.hwans.screenshareserver.mapper;

import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.entity.sharing.SharingChannel;
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
    public abstract SharingChannel toEntity(CreateChannelRequestDto createChannelRequestDto);

    @Named("encodePassword")
    String encoderPassword(String password) {
        if (password == null)
            return null;

        return passwordEncoder.encode(password);
    }
}
