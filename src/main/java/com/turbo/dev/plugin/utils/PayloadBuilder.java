package com.turbo.dev.plugin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class PayloadBuilder {

    private static final Pattern CLASS_START_PATTERN =
            Pattern.compile("<<<CLASS_START");

    public static Map<String, Object> buildPayload(
            String combinedCode,
            String batchId,
            String compressedData
    ) {

        Map<String, Object> payload = new HashMap<>();

        // batchId
        payload.put("batchId", batchId);

        // dateTime (equivalent to Date.now())
        payload.put("dateTime", String.valueOf(System.currentTimeMillis()));

        // compressed data
        payload.put("data", compressedData);

        // original size (MB)
        payload.put("originalSize",
                SizeUtil.getSizeInMB(combinedCode)
        );

        // compressed size (MB)
        payload.put("compressedSize",
                SizeUtil.getSizeInMB(compressedData)
        );

        // total files (count CLASS_START occurrences)
        Matcher matcher = CLASS_START_PATTERN.matcher(combinedCode);

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        payload.put("totalFiles", count);
        return payload;
    }
}
