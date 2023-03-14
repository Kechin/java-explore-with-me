package ru.practicum.main.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.user.Dto.UserDto;

import java.util.ArrayList;
import java.util.List;

interface  UserService {
    @Transactional
    UserDto create(UserDto userDto);

    @Transactional
    UserDto update(UserDto userDto, Long id);

    UserDto get(Long userId);

    @Transactional
    void delete(Long userId);

    List<UserDto> getUsers(ArrayList<Long> ids, Integer from, Integer size);
}
