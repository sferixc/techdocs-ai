package com.eric.techdocsai.document;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("api/documents")
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentController {

	private DocumentRepository documentRepository;

	public DocumentController(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@GetMapping
	public List<DocumentSummaryResponse> getDocuments() {
		return documentRepository.findAll()
				.stream()
				.sorted(Comparator.comparing(DocumentEntity::getCreatedAt).reversed())
				.map(DocumentSummaryResponse::fromEntity)
				.toList();
	}

}
