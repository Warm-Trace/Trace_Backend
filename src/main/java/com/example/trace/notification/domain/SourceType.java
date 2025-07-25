package com.example.trace.notification.domain;

public enum SourceType {
    POST, COMMENT, MISSION, EMOTION;

    public static SourceType fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return SourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown SourceType: " + value);
        }
    }
}