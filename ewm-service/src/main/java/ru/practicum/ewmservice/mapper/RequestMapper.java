package ru.practicum.ewmservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.model.Request;

import java.time.LocalDateTime;

import static ru.practicum.ewmservice.util.Const.TIME_FORMATTER;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto toDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated().format(TIME_FORMATTER))
                .requester(request.getId())
                .status(request.getStatus()).build();
    }

    public static Request toModel(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                .event(null)
                .created(LocalDateTime.parse(participationRequestDto.getCreated(), TIME_FORMATTER))
                .requester(null)
                .status(participationRequestDto.getStatus()).build();
    }
}
