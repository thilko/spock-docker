package com.thilko.spockdocker

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface PortBinding {

    String exposingPort();
    String hostIp();
    String hostPort();
}