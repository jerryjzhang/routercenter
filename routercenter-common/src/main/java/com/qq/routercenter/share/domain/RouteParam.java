package com.qq.routercenter.share.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteParam {
    /**
     * Defines the name of the route parameter whose value will be
     * used to route invocation to certain providers based on some
     * route rules.
     */
    String value();
}
