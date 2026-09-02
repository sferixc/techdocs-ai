package com.eric.techdocsai.document;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfDocumentParser {

	/**
	 * Parses a PDF document into a list of pages.
	 * @param resource
	 * @return
	 * @throws IOException
	 */

	public List<ParsedPage> parse(Resource resource) throws IOException {
		List<ParsedPage> pages = new ArrayList<>();

		try (var input = resource.getInputStream();
			 PDDocument document = Loader.loadPDF(input.readAllBytes())) {

			PDFTextStripper stripper = new PDFTextStripper();

			for (int page = 1; page <= document.getNumberOfPages(); page++) {
				stripper.setStartPage(page);
				stripper.setEndPage(page);

				String text = stripper.getText(document);
				pages.add(new ParsedPage(page, text));
			}
		}

		return pages;
	}
}