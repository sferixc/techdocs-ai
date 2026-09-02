package com.eric.techdocsai.document;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextChunker {

	private static final int TARGET_WORDS = 250;

	public List<TextChunk> chunk(List<ParsedPage> pages) {
		StringBuilder fullText = new StringBuilder();
		List<Integer> pageStarts = new ArrayList<>();

		StringBuilder currentChunk = new StringBuilder();
		int currentWordCount = 0;

		for (ParsedPage page : pages) {
			String text = normalizeWhitespace(page.text());

			if (!fullText.isEmpty()) {
				fullText.append(" ");
			}

			pageStarts.add(fullText.length());
			fullText.append(text);
		}

		String text = fullText.toString();

		BreakIterator iterator =
				BreakIterator.getSentenceInstance(Locale.ENGLISH);

		iterator.setText(text);

		int start = iterator.first();

		for (int end = iterator.next();
			 end != BreakIterator.DONE;
			 start = end, end = iterator.next()) {

			String sentence = text.substring(start, end).strip();

			if (sentence.isEmpty()) {
				continue;
			}

		}

		return null;
	}

	private String normalizeWhitespace(String text) {
		return text
				.replaceAll("\\s+", " ")
				.strip();
	}

	private int countWords(String text) {
		if (text.isBlank()) {
			return 0;
		}

		return text.strip().split("\\s+").length;
	}
}
