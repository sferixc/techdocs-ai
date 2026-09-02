package com.eric.techdocsai.document;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextChunker {

	private static final int TARGET_WORDS = 250;

	public List<TextChunk> chunk(List<ParsedPage> pages) {
		List<TextChunk> chunks = new ArrayList<>();

		StringBuilder fullText = new StringBuilder();
		List<Integer> pageStarts = new ArrayList<>();

		StringBuilder currentChunk = new StringBuilder();
		int currentWordCount = 0;

		int chunkStartPage = 0;
		int chunkEndPage = 0;

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

		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {

			int sentenceStart = start;
			int sentenceEnd = end;

			while (sentenceStart < sentenceEnd
					&& Character.isWhitespace(text.charAt(sentenceStart))) {
				sentenceStart++;
			}

			while (sentenceEnd > sentenceStart
					&& Character.isWhitespace(text.charAt(sentenceEnd - 1))) {
				sentenceEnd--;
			}

			if (sentenceStart == sentenceEnd) {
				continue;
			}

			String sentence = text.substring(sentenceStart, sentenceEnd);
			int sentenceWordCount = countWords(sentence);


			if (currentWordCount > 0
					&& currentWordCount + sentenceWordCount > TARGET_WORDS) {

				chunks.add(new TextChunk(
						chunks.size(),
						currentChunk.toString(),
						chunkStartPage,
						chunkEndPage,
						currentWordCount
				));

				currentChunk.setLength(0);
				currentWordCount = 0;
			}

			if (currentWordCount == 0) {
				chunkStartPage = findPage(sentenceStart, pageStarts, pages);
			} else {
				currentChunk.append(" ");
			}

			currentChunk.append(sentence);
			currentWordCount += sentenceWordCount;

			chunkEndPage = findPage(sentenceEnd - 1, pageStarts, pages);


		}

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

	private int findPage(int position, List<Integer> pageStarts, List<ParsedPage> pages
	) {
		for (int i = pageStarts.size() - 1; i >= 0; i--) {
			if (position >= pageStarts.get(i)) {
				return pages.get(i).pageNumber();
			}
		}

		throw new IllegalArgumentException("Position without page");
	}
}
