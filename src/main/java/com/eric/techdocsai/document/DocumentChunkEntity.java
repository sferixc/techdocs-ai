package com.eric.techdocsai.document;

import jakarta.persistence.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

	private Integer startPage;

	private Integer endPage;

	private String sectionTitle;

	private int wordCount;

	@JdbcTypeCode(SqlTypes.VECTOR)
	@Array(length = 768)
	@Column(name = "embedding")
	private float[] embedding;

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
				Integer startPage,
				Integer endPage,
				String sectionTitle,
				int wordCount,
				float[] embedding
		) {
			this.document = document;
			this.chunkIndex = chunkIndex;
			this.content = content;
			this.startPage = startPage;
			this.endPage = endPage;
			this.sectionTitle = sectionTitle;
			this.wordCount = wordCount;
			this.createdAt = Instant.now();
			this.embedding = embedding;
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

	public Integer getStartPage() {
		return startPage;
	}

	public Integer getEndPage() {
		return endPage;
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

	public void setEmbedding(float[] embedding) {
		this.embedding = embedding;
	}
}