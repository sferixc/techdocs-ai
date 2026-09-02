package com.eric.techdocsai.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, Long> {

	@Query(value = """
        SELECT
            c.id AS "chunkId",
            d.id AS "documentId",
            d.title AS "documentTitle",
            c.content AS "content",
            c.start_page AS "startPage",
            c.end_page AS "endPage",
            c.embedding <=> CAST(:embedding AS vector) AS "distance"
        FROM document_chunks c
        JOIN documents d ON d.id = c.document_id
        WHERE c.embedding IS NOT NULL
        ORDER BY c.embedding <=> CAST(:embedding AS vector)
        LIMIT 3
        """, nativeQuery = true)
	List<ChunkSearchMatch> findClosestChunks(
			@Param("embedding") String embedding
	);
}
