package ru.practicum.event.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventNewDto {
    private Long id;
    @NotBlank
    @Size(max=2048)
    private String annotation;
    private Long category;
    @NotBlank
    @Size(max=128000)
    private String description;
    private LocationDto location;
    @NotNull
    private Boolean paid;
    @NotBlank
    @Size(max=1024)
    private String title;
    @PositiveOrZero
    private Integer participantLimit;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private Boolean requestModeration;

}
