package com.otr.plugins.qualityGate.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class MemoryScanner {

    public static void howByteUsed() {
        howByteUsed("Memory scanner");
    }

    public static void howByteUsed(final String stage) {
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        try {
            int usedMegabytes = (int) Math.abs(usedBytes / 1048576);
            log.info("{}, memory used {} mb", stage, usedMegabytes);
        } catch (Exception e) {
            log.info("{}, memory used {} byte", stage, usedBytes);
        }
    }
}
