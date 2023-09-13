package ru.practicum.ewmservice.util;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Const {
    //public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
