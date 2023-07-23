package com.formos.service.utils;

import com.nimbusds.jose.shaded.gson.JsonObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CommonUtils {

    public static <T> T getOrDefault(T data, T defaultData) {
        return Objects.nonNull(data) ? data : defaultData;
    }

    public static String getStringPropertyOfJsonObject(JsonObject jsonObject, String propertyName) {
        return jsonObject.has(propertyName) ? jsonObject.get(propertyName).getAsString() : null;
    }

    public static String formatToDateTimeFromTimestamp(String timestamp, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault()));
    }
}
