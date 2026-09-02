package com.eric.techdocsai.document;

import com.eric.techdocsai.embedding.OllamaEmbeddingClient;
import com.eric.techdocsai.embedding.OllamaEmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DocumentSearchService {

	private final DocumentChunkRepository documentChunkRepository;
	private final OllamaEmbeddingClient ollamaEmbeddingClient;

	public DocumentSearchService(DocumentChunkRepository documentChunkRepository, OllamaEmbeddingClient ollamaEmbeddingClient) {
		this.documentChunkRepository = documentChunkRepository;
		this.ollamaEmbeddingClient = ollamaEmbeddingClient;
	}

	public List<DocumentSearchResult> search(String query) {
		OllamaEmbeddingResponse response = ollamaEmbeddingClient.createEmbedding(query);
		float[] queryEmbedding = response.embeddings()[0];

		var matches = documentChunkRepository.findClosestChunks(
				Arrays.toString(queryEmbedding)
		);

		return matches.stream()
				.map(match -> new DocumentSearchResult(
						match.getDocumentId(),
						match.getDocumentTitle(),
						match.getChunkId(),
						match.getContent(),
						match.getStartPage(),
						match.getEndPage(),
						1.0 - match.getDistance()
				))
				.toList();

	}
}
