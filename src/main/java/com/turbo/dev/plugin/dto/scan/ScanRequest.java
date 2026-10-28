package com.turbo.dev.plugin.dto.scan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanRequest {
	private String batchId;
	private String dateTime;
	private String data;
	private Double originalSize;
	private Double compressedSize;
	private Integer totalFiles;
}