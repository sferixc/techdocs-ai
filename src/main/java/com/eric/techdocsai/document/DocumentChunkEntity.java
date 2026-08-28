package com.eric.techdocsai.document;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "document_chunks")
public class DocumentChunkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int chunkIndex;

	@Column(columnDefinition = "TEXT")
	private String content;

	private Integer pageNumber;

	private String sectionTitle;

	private int wordCount;

	private Instant createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = false)
	private DocumentEntity document;

	protected DocumentChunkEntity() {
	}

	public DocumentChunkEntity(
			DocumentEntity document,
			int chunkIndex,
			String content,
			Integer pageNumber,
			String sectionTitle,
			int wordCount
	) {
		this.document = document;
		this.chunkIndex = chunkIndex;
		this.content = content;
		this.pageNumber = pageNumber;
		this.sectionTitle = sectionTitle;
		this.wordCount = wordCount;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public int getChunkIndex() {
		return chunkIndex;
	}

	public String getContent() {
		return content;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public int getWordCount() {
		return wordCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public DocumentEntity getDocument() {
		return document;
	}
}