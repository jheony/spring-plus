package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserGetProfileImagesResponse {

    private final String profileImages;

    public UserGetProfileImagesResponse(String profileImages) {
        this.profileImages = profileImages;
    }
}
