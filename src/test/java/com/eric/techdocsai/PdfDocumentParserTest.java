package com.eric.techdocsai.document;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PdfDocumentParserTest {

	@Test
	void extractTextFromPdf() throws Exception {
		PdfDocumentParser parser = new PdfDocumentParser();

		var resource = new ClassPathResource("useful_ml.pdf");
		var pages = parser.parse(resource);

		assertFalse(pages.isEmpty());

		StringBuilder output = new StringBuilder();

		for (ParsedPage page : pages) {
			output.append("=== PAGE ")
					.append(page.pageNumber())
					.append(" ===\n\n")
					.append(page.text())
					.append("\n\n");
		}

		Path destination = Path.of("target", "pdf-preview", "useful_ml.txt");

		Files.createDirectories(destination.getParent());
		Files.writeString(destination, output, StandardCharsets.UTF_8);

		System.out.println("Text saved in " + destination.toAbsolutePath());
	}
}