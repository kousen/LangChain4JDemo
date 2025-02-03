package com.kousenit.tools;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Calculator {
    private final Logger logger = LoggerFactory.getLogger(Calculator.class);

    @Tool("Returns the length of a string")
    public int stringLength(String s) {
        logger.info("Called stringLength with s='{}'", s);
        return s.length();
    }

    @Tool("Adds two numbers")
    public int add(int a, int b) {
        logger.info("Called add with a={}, b={}", a, b);
        return a + b;
    }

    @Tool("Computes the square root of a number")
    public double sqrt(int x) {
        logger.info("Called sqrt with x={}", x);
        return Math.sqrt(x);
    }
}
