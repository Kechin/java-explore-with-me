package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Create;
import ru.practicum.user.Dto.UserDto;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    UserDto create(@Validated({Create.class}) @RequestBody UserDto user) {
        return userService.create(user);
    }


    @GetMapping()
    List<UserDto> get(@RequestParam ArrayList<Long> ids, @RequestParam(required = false) Integer from,
                      @RequestParam(required = false) Integer size) {
        return userService.getUsers(ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
