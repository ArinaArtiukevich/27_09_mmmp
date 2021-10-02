package com.jwd.service.serviceLogic;

import com.jwd.service.domain.User;
import com.jwd.service.domain.UserDto;
import com.jwd.service.exception.ServiceException;

import java.util.List;

public interface UserService {

    /**
     * returns all users in our app
     * @return List<UserDto>
     */
    List<UserDto> getUsers();

    /**
     *
     * @param user - to be saved, received from UI
     * @return UserDto to display saved user
     */
    UserDto registerUser(User user) throws ServiceException;

}
