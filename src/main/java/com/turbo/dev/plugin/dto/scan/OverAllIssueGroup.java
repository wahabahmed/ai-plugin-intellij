package com.turbo.dev.plugin.dto.scan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverAllIssueGroup {
	private String type;
	private String overAllSeverity;
	private List<Issue> issues;
}
