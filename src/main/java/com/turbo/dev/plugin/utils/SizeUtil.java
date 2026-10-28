package com.turbo.dev.plugin.utils;

import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SizeUtil {

    public static double getSizeInMB(String data) {

        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        double mb = bytes.length / (1024.0 * 1024.0);

        BigDecimal rounded = new BigDecimal(mb)
                .setScale(4, RoundingMode.HALF_UP);

        return rounded.doubleValue();
    }
}
