package com.eric.techdocsai.document;

import com.eric.techdocsai.embedding.OllamaEmbeddingClient;
import com.eric.techdocsai.embedding.OllamaEmbeddingResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DocumentDataSeeder implements CommandLineRunner {

	private final DocumentRepository documentRepository;
	private final OllamaEmbeddingClient ollamaEmbeddingClient;

	public DocumentDataSeeder(DocumentRepository documentRepository, OllamaEmbeddingClient ollamaEmbeddingClient) {
		this.documentRepository = documentRepository;
		this.ollamaEmbeddingClient = ollamaEmbeddingClient;
	}

	@Override
	public void run(String... args) {

		OllamaEmbeddingResponse responseObject =  ollamaEmbeddingClient.createEmbedding("something sum sum");

		System.out.println(responseObject.embeddings().get(0).size());

		if (documentRepository.count() > 0) {
			return;
		}

		DocumentEntity springDocument = new DocumentEntity(
				"Spring Framework Reference",
				"Spring Team",
				DocumentFileType.HTML,
				"preloaded/spring-framework-reference.html",
				12450
		);

		springDocument.getChunks().add(new DocumentChunkEntity(
				springDocument,
				0,
				"Dependency injection is a design pattern where an object receives its dependencies from an external source instead of creating them itself.",
				null,
				"Core Technologies - Dependency Injection",
				21
		));

		springDocument.getChunks().add(new DocumentChunkEntity(
				springDocument,
				1,
				"The Spring IoC container is responsible for instantiating, configuring, and assembling application objects known as beans.",
				null,
				"Core Technologies - IoC Container",
				17
		));

		DocumentEntity mathDocument = new DocumentEntity(
				"Linear Algebra Notes",
				"Course Staff",
				DocumentFileType.PDF,
				"preloaded/linear-algebra-notes.pdf",
				8300
		);

		mathDocument.getChunks().add(new DocumentChunkEntity(
				mathDocument,
				0,
				"A vector space is a set of objects called vectors, together with operations of addition and scalar multiplication.",
				3,
				"Vector Spaces",
				18
		));

		mathDocument.getChunks().add(new DocumentChunkEntity(
				mathDocument,
				1,
				"A matrix transformation maps vectors from one vector space to another while preserving linear combinations.",
				8,
				"Linear Transformations",
				15
		));

		documentRepository.save(springDocument);
		documentRepository.save(mathDocument);
	}
}