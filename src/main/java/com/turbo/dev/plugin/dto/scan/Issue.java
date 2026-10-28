package com.turbo.dev.plugin.dto.scan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Issue {
	private String severity;
	private String message;
	private String codeWithLineNumber;
	private String solution;
	private boolean requiredFix;
}
