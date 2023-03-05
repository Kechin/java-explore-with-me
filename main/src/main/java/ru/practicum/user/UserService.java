package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.Dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;


    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("Запрос на добавления нового пользователя" + userDto);
        if (userRepository.findFirstByNameIs(userDto.getName()) == null) {
            User user = userRepository.save(UserMapper.toUser(userDto));
            return UserMapper.toUserDto(user);
        }
        throw new ConflictException("Пользователь с данным именем уже существует");
    }

    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        User oldUser = getUser(id);
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);

        return UserMapper.toUserDto(oldUser);
    }

    public UserDto get(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }


    @Transactional
    public void delete(Long userId) {
        getUser(userId);
        log.info("Запрос на удаления пользователя " + userId);
        userRepository.deleteById(userId);
    }

    public List<UserDto> getUsers(ArrayList<Long> ids) {
        return UserMapper.userDtos(userRepository.findAllByIdIn(ids));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }

}
