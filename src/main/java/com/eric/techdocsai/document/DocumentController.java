package com.eric.techdocsai.document;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("api/documents")
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentController {

	private DocumentRepository documentRepository;
	private DocumentSearchService documentSearchService;

	public DocumentController(DocumentRepository documentRepository, DocumentSearchService documentSearchService) {
		this.documentRepository = documentRepository;
		this.documentSearchService = documentSearchService;
	}

	@GetMapping
	public List<DocumentSummaryResponse> getDocuments() {
		return documentRepository.findAll()
				.stream()
				.sorted(Comparator.comparing(DocumentEntity::getCreatedAt).reversed())
				.map(DocumentSummaryResponse::fromEntity)
				.toList();
	}

	@PostMapping("/search")
	public List<DocumentSearchResult> search(@Valid @RequestBody DocumentSearchRequest request) {
		return documentSearchService.search(request.query());
	}

}
