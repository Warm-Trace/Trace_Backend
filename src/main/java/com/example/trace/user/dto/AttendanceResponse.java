package com.example.trace.user.dto;

import java.time.LocalDate;

public record AttendanceResponse(LocalDate date, boolean checked, long pointsAdded, long balance) {
}
