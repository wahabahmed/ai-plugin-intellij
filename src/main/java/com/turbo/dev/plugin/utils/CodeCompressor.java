package com.turbo.dev.plugin.utils;

import java.util.Base64;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public class CodeCompressor {

    public static String getCompressedData(String data, String batchId) {

        if (data == null) data = "";
        if (batchId == null) batchId = "";

        try {
            // 1. GZIP compress
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (GZIPOutputStream gzipOutputStream =
                         new GZIPOutputStream(byteArrayOutputStream)) {

                gzipOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            }

            byte[] compressedBytes = byteArrayOutputStream.toByteArray();

            // 2. Base64 encode
            String base64 = Base64.getEncoder().encodeToString(compressedBytes);
            // 3. Append batchId (same behavior as TS)
            return base64 + batchId;

        } catch (Exception e) {
            throw new RuntimeException("GZIP compression failed", e);
        }
    }
}

