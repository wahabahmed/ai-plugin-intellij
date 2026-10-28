package com.turbo.dev.plugin.dto;

import com.turbo.dev.plugin.dto.scan.OverAllIssueGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanResult {
	private String overAllStatus;
	private int overallScore;
	private List<OverAllIssueGroup> overAllIssues;
}
