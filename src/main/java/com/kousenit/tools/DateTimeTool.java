package com.kousenit.tools;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class DateTimeTool {
    private final Logger logger = LoggerFactory.getLogger(DateTimeTool.class);

    @Tool("Get the number of years in the future")
    public int yearsFromNow(int years) {
        logger.info("Called yearsFromNow with years={}", years);
        return LocalDate.now().plusYears(years).getYear();
    }
}
