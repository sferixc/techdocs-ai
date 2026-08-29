package com.eric.techdocsai.embedding;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OllamaEmbeddingClient {

	private RestClient client;


	public OllamaEmbeddingClient() {
		this.client = RestClient.builder().baseUrl("http://localhost:11434").build();
	}

	public OllamaEmbeddingResponse createEmbedding (String content){

		OllamaEmbeddingRequest request = new OllamaEmbeddingRequest("embeddinggemma", content);

		OllamaEmbeddingResponse response = client.post().uri("api/embed").body(request).retrieve()
				.body(OllamaEmbeddingResponse.class);

		return response;
	}

}
