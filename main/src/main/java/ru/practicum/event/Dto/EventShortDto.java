package ru.practicum.event.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.Dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private String description;
    private UserDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;


}
