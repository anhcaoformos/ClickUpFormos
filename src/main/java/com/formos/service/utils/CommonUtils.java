package com.formos.service.utils;

import com.formos.config.Constants;
import com.nimbusds.jose.shaded.gson.JsonObject;
import java.awt.*;
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
        if (Objects.isNull(timestamp)) {
            return "";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault()));
    }

    public static String decreaseSaturation(String colorName) {
        Color color = getColorFromName(colorName);
        if (Objects.isNull(color)) {
            return "white";
        }
        float factor = 0.9f;
        float[] hsl = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsl);
        hsl[1] = Math.max(0f, hsl[1] - factor);
        Color modifiedColor = new Color(Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]));
        return "#" + Integer.toHexString(modifiedColor.getRGB()).substring(2);
    }

    public static boolean isImage(String extension) {
        return Constants.IMAGE_EXTENSION.contains(extension);
    }

    public static boolean getBooleanValue(Object data) {
        if (data instanceof String) {
            return Boolean.parseBoolean(data.toString());
        } else if (data instanceof Boolean) {
            return (Boolean) data;
        } else if (data instanceof Integer) {
            return (Integer) data != 0;
        } else {
            return false;
        }
    }

    private static Color getColorFromName(String colorName) {
        try {
            return (Color) Color.class.getField(colorName.toLowerCase()).get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
