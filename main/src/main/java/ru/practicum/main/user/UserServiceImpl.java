package ru.practicum.main.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.user.model.UserMapper;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.Dto.UserDto;
import ru.practicum.main.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("Запрос на добавления нового пользователя {}" + userDto);
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        log.info("Запрос на обновления данных пользователя id: {} user:", id, userDto);
        User oldUser = getUser(id);
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank()) {
                throw new ValidationException("Email is blank.");
            }
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        log.info("Запрос на обновления данных пользователя id {} user {}", id, userDto);
        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        getUser(userId);
        log.info("Запрос на удаления пользователя  {} ", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(ArrayList<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        if (ids == null) {
            return UserMapper.userDtos(userRepository.findAll(pageable).toList());
        }
        return UserMapper.userDtos(userRepository.findAllByIdIn(pageable, ids).toList());
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }

}
