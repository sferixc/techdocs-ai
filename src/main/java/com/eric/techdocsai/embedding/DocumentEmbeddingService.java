package com.eric.techdocsai.embedding;

import com.eric.techdocsai.document.ParsedPage;
import com.eric.techdocsai.document.TextChunk;
import com.eric.techdocsai.document.TextChunker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentEmbeddingService {

	private OllamaEmbeddingClient ollamaEmbeddingClient;
	private TextChunker textChunker = new TextChunker();

	public DocumentEmbeddingService(OllamaEmbeddingClient ollamaEmbeddingClient) {
		this.ollamaEmbeddingClient = ollamaEmbeddingClient;
	}

	public List<EmbeddedChunk> embed(List<ParsedPage> pages){
		List<TextChunk> chunks = textChunker.chunk(pages);
		List<EmbeddedChunk> results = new ArrayList<>();

		for(TextChunk chunk : chunks){
			OllamaEmbeddingResponse response = ollamaEmbeddingClient.createEmbedding(chunk.content());

			if (response == null
					|| response.embeddings() == null
					|| response.embeddings()[0].length != 1
					|| response.embeddings()[0]== null){
				throw new IllegalStateException(
						"Missing embedding for chunk " + chunk.chunkIndex()
				);
			}
			results.add(new EmbeddedChunk(chunk, response.embeddings()[0])); //because response has List<List<Double>>
		}

		return results;
	}
}
