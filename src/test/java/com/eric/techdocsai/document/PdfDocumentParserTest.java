package com.eric.techdocsai.document;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	void keepsSentenceTogetherAcrossPages() {
		var pages = List.of(
				new ParsedPage(1, "Machine learning models learn"),
				new ParsedPage(2, "from examples.")
		);

		var chunks = new TextChunker().chunk(pages);

		assertEquals(1, chunks.size());

		TextChunk chunk = chunks.getFirst();

		assertAll(
				() -> assertEquals(
						"Machine learning models learn from examples.",
						chunk.content()
				),
				() -> assertEquals(0, chunk.chunkIndex()),
				() -> assertEquals(1, chunk.startPage()),
				() -> assertEquals(2, chunk.endPage()),
				() -> assertEquals(6, chunk.wordCount())
		);
	}

	@Test
	void startsNewChunkBeforeExceedingWordLimit() {
		String firstSentence = "word ".repeat(239) + "ends.";
		String secondSentence = "Another sentence has five words.";

		var pages = List.of(
				new ParsedPage(
						1,
						firstSentence + " "
								+ secondSentence + " "
								+ secondSentence + " "
								+ secondSentence
				)
		);

		var chunks = new TextChunker().chunk(pages);

		assertEquals(2, chunks.size());

		assertAll(
				() -> assertEquals(250, chunks.get(0).wordCount()),
				() -> assertEquals(5, chunks.get(1).wordCount()),
				() -> assertEquals(
						firstSentence + " "
								+ secondSentence + " "
								+ secondSentence,
						chunks.get(0).content()
				),
				() -> assertEquals(
						secondSentence,
						chunks.get(1).content()
				)
		);
	}
}