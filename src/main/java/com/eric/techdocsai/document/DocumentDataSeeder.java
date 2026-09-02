package com.eric.techdocsai.document;

import com.eric.techdocsai.embedding.OllamaEmbeddingClient;
import com.eric.techdocsai.embedding.OllamaEmbeddingResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentDataSeeder implements CommandLineRunner {

	private final DocumentRepository documentRepository;
	private final OllamaEmbeddingClient ollamaEmbeddingClient;
	private final PdfDocumentParser pdfDocumentParser = new PdfDocumentParser();
	private final TextChunker textChunker = new TextChunker();


	public DocumentDataSeeder(DocumentRepository documentRepository, OllamaEmbeddingClient ollamaEmbeddingClient) {
		this.documentRepository = documentRepository;
		this.ollamaEmbeddingClient = ollamaEmbeddingClient;
	}

	@Override
	public void run(String... args) {

		List<ParsedPage> pages = new ArrayList<>();

		Resource resource = new ClassPathResource("useful_ml.pdf");
		try{
			pages = pdfDocumentParser.parse(resource);
		}
		catch (IOException e){

		}

		List<TextChunk> chunks = textChunker.chunk(pages);

		DocumentEntity document = new DocumentEntity();

		int index = 0;

		for(TextChunk chunk : chunks){
			DocumentChunkEntity chunkEntity = new DocumentChunkEntity(
					document, chunk.chunkIndex(), chunk.content()
					,chunk.startPage(), chunk.endPage(), "", chunk.wordCount()
			); //leave out section title for now for testing purposes

			document.addChunk(chunkEntity);

		}

		documentRepository.save(document);



	}
}