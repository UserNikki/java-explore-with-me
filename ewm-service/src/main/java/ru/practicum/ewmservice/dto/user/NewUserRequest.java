package ru.practicum.ewmservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewUserRequest {

    @Length(max = 254, min = 6)
    private String email;
    @Length(max = 250, min = 2)
    private String name;
}
