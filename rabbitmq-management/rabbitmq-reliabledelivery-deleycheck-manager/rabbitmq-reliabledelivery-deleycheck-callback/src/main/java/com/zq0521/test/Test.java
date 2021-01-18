package com.zq0521.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Test {

    private final static AtomicInteger nextIndex = new AtomicInteger();

    private static int increamentAndGetModulo(int modulo) {
        log.info("begin--->");
        for (; ; ) {
            int current = nextIndex.get();
            int next = (current + 1) % modulo;
            if (nextIndex.compareAndSet(current, next) && current < modulo) {
                return current;
            }
        }

    }

    public static void main(String[] args) {
        increamentAndGetModulo(3);


    }
}
