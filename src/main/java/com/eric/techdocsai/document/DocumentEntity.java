package com.eric.techdocsai.document;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "documents")
public class DocumentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String author;

	@Enumerated
	private DocumentFileType fileType;

	private String sourcePath;

	private int wordCount;

	private Instant createdAt;

	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentChunkEntity> chunks = new ArrayList<>();

	protected DocumentEntity() {

	}

	public DocumentEntity(String title, String author, DocumentFileType fileType, String sourcePath, int wordCount) {
		this.title = title;
		this.author = author;
		this.fileType = fileType;
		this.sourcePath = sourcePath;
		this.wordCount = wordCount;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public DocumentFileType getFileType() {
		return fileType;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public int getWordCount() {
		return wordCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public List<DocumentChunkEntity> getChunks() {
		return chunks;
	}

}
