package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Create;
import ru.practicum.user.Dto.UserDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    UserDto create(@Validated({Create.class}) @RequestBody UserDto user) {
        return userService.create(user);
    }

    @GetMapping()
    List<UserDto> get(@RequestParam(required = false) ArrayList<Long> ids,
                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                      @PositiveOrZero @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(ids,from,size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
