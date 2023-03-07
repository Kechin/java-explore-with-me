package ru.practicum.event.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.Dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id;
    @NotBlank
    @Size(max=2048)
    private String annotation;
    private CategoryDto category;
    @NotBlank
    @Size(max=128000)
    private String description;
    private UserDto initiator;
    private Boolean paid;
    @NotBlank
    @Size(max=1024)
    private String title;
    private Integer views;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;


}
