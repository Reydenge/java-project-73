package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {
    User createNewUser(UserDto userDto);
    User updateUser(Long id, UserDto userDto);
    String getCurrentUserName();
    User getCurrentUser();

    List<User> getAll();
    User getUserById(Long id);
}
