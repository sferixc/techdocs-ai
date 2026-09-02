package com.eric.techdocsai.document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record DocumentSearchRequest(
		@NotBlank
		String query
) {
}
