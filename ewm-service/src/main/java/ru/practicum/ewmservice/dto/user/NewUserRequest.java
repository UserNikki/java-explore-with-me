package ru.practicum.ewmservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewUserRequest {
    @NotBlank
    @Length(max = 250, min = 2)
    private String name;
    @NotBlank
    @Email
    @Length(max = 254, min = 6)
    private String email;
}
