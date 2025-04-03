package org.arthub.backend.mapper;

import lombok.experimental.UtilityClass;
import org.arthub.backend.dto.UserProfileDTO;
import org.arthub.backend.entity.User;

/**
 * Utility class for converting a {@link User} entity to a {@link UserProfileDTO}.
 */
@UtilityClass
public class UserMapper {
    /**
     * Converts a User object to a UserProfileDTO.
     *
     * @param user the User object to be converted.
     * @return the UserProfileDTO containing user data.
     */
    public static UserProfileDTO toUserProfileDTO(final User user) {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPosts(user.getPosts());

        return dto;
    }
}
