package ru.practicum.ewmservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewmservice.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @Length(max = 2000, min = 20)
    private String annotation;
    private Long category;
    @Length(max = 7000, min = 20)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @Length(min = 3, max = 120)
    private String title;
}
