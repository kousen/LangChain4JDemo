package com.kousenit.services;

import dev.langchain4j.service.UserMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface DateTimeExtractor {
    @UserMessage("Extract date from {{it}}")
    LocalDate extractDateFrom(String text);

    @UserMessage("Extract time from {{it}}")
    LocalTime extractTimeFrom(String text);

    @UserMessage("Extract date and time from {{it}}")
    LocalDateTime extractDateTimeFrom(String text);
}
