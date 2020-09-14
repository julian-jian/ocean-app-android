package com.sky.lamp.adapter;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
public @interface RecyclerItemViewId {
    int value();//我们注解后面使用id值
}
