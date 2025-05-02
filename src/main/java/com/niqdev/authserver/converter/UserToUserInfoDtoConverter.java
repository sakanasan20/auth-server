package com.niqdev.authserver.converter;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.niqdev.authserver.dto.UserInfoDto;
import com.niqdev.authserver.entity.User;

@Component
public class UserToUserInfoDtoConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public UserInfoDto convert(User user) {
        return new UserInfoDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.isEnabled(),
            user.isAccountNonLocked(),
            user.isAccountNonExpired(),
            user.isCredentialsNonExpired(),
            user.getCreatedAt() != null ? user.getCreatedAt().format(FORMATTER) : null,
            user.getCreatedBy(),
            user.getUpdatedAt() != null ? user.getUpdatedAt().format(FORMATTER) : null,
            user.getUpdatedBy()
        );
    }
}
