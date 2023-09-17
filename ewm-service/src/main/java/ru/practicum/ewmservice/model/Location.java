package ru.practicum.ewmservice.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Location {
    private Float lat;
    private Float lon;
}
