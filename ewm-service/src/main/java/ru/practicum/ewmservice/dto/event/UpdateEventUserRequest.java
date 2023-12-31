package ru.practicum.ewmservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewmservice.enums.StateAction;
import ru.practicum.ewmservice.model.Location;

import java.time.LocalDateTime;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@RequiredArgsConstructor
//@Builder
public class UpdateEventUserRequest {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Length(min = 3, max = 120)
    private String title;
}
