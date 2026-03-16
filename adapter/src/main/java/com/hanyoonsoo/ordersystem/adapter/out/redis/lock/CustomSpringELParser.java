package com.hanyoonsoo.ordersystem.adapter.out.redis.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class CustomSpringELParser {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private CustomSpringELParser() {
    }

    public static String getDynamicValue(String[] parameterNames, Object[] args, String expression) {
        if (expression == null || expression.isBlank()) {
            return "";
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        Object value = PARSER.parseExpression(expression).getValue(context);
        return value == null ? "" : value.toString();
    }
}
