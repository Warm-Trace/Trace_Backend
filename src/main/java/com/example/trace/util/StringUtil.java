package com.example.trace.util;

public class StringUtil {
    public static final int PREVIEW_CONTENT_LENGTH = 100;
    public static final int SHORT_PREVIEW_CONTENT_LENGTH = 20;

    public static String truncate(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        return content.length() <= maxLength
                ? content
                : content.substring(0, maxLength) + "...";
    }

    public static String truncate(String content) {
        return truncate(content, PREVIEW_CONTENT_LENGTH);
    }

    public static String truncateLess(String content) {
        return truncate(content, SHORT_PREVIEW_CONTENT_LENGTH);
    }
}