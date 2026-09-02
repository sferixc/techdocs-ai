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

		if(documentRepository.count() > 0) return;

		List<ParsedPage> pages = new ArrayList<>();

		Resource resource = new ClassPathResource("useful_ml.pdf");
		try{
			pages = pdfDocumentParser.parse(resource);
		}
		catch (IOException e){
			throw new IllegalStateException("Could not parse useful_ml.pdf", e);
		}

		List<TextChunk> chunks = textChunker.chunk(pages);

		int totalWords = chunks.stream()
				.mapToInt(TextChunk::wordCount)
				.sum();

		DocumentEntity document = new DocumentEntity(
				"A Few Useful Things to Know about Machine Learning",
				"Pedro Domingos",
				DocumentFileType.PDF,
				"useful_ml.pdf",
				totalWords
		);

		int index = 0;

		for(TextChunk chunk : chunks){
			DocumentChunkEntity chunkEntity = new DocumentChunkEntity(
					document, chunk.chunkIndex(), chunk.content()
					,chunk.startPage(), chunk.endPage(), "", chunk.wordCount(), null
			); //leave out section title for now for testing purposes

			OllamaEmbeddingResponse response = ollamaEmbeddingClient.createEmbedding(chunk.content());

			chunkEntity.setEmbedding(response.embeddings()[0]);

			document.addChunk(chunkEntity);

		}

		documentRepository.save(document);



	}

}