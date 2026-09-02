package com.eric.techdocsai.document;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop", showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class DocumentChunkRepositoryTest {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("pgvector/pgvector:pg16")
            .withInitScript("pgvector-test-init.sql");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", postgres::getJdbcUrl);
        properties.add("spring.datasource.username", postgres::getUsername);
        properties.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired DocumentRepository documents;
    @Autowired DocumentChunkRepository chunks;

    @Test
    void returnsClosestThreeAcrossDocumentsWithCorrectProjection() {
        var first = document("First document");
        var second = document("Second document");
        // Insert in a different order from relevance to catch missing/incorrect ORDER BY.
        chunk(first, 0, "Opposite", vector(-1, 0));
        var third = chunk(second, 0, "Orthogonal", vector(0, 1));
        var best = chunk(first, 1, "Closest passage", vector(1, 0));
        var runnerUp = chunk(first, 2, "Second closest", vector(0.8f, 0.6f));
        chunk(second, 1, "No embedding", null);
        documents.saveAllAndFlush(List.of(first, second));

        var matches = chunks.findClosestChunks(Arrays.toString(vector(1, 0)));

        assertEquals(List.of(best.getId(), runnerUp.getId(), third.getId()),
                matches.stream().map(ChunkSearchMatch::getChunkId).toList());
        var match = matches.getFirst();
        assertAll(
                () -> assertEquals(first.getId(), match.getDocumentId()),
                () -> assertEquals("First document", match.getDocumentTitle()),
                () -> assertEquals("Closest passage", match.getContent()),
                () -> assertEquals(2, match.getStartPage()),
                () -> assertEquals(3, match.getEndPage()),
                () -> assertEquals(0.0, match.getDistance(), 1e-6),
                () -> assertEquals(0.2, matches.get(1).getDistance(), 1e-6),
                () -> assertEquals(1.0, matches.get(2).getDistance(), 1e-6)
        );
    }

    @Test
    void returnsNoMatchesForEmptyDatabase() {
        assertTrue(chunks.findClosestChunks(Arrays.toString(vector(1, 0))).isEmpty());
    }

    @Test
    void returnsFewerThanThreeAndExcludesMissingEmbeddings() {
        var document = document("Small collection");
        var searchable = chunk(document, 0, "Searchable", vector(1, 0));
        chunk(document, 1, "Not embedded", null);
        documents.saveAndFlush(document);

        var matches = chunks.findClosestChunks(Arrays.toString(vector(1, 0)));
        assertEquals(1, matches.size());
        assertEquals(searchable.getId(), matches.getFirst().getChunkId());
    }

    private DocumentEntity document(String title) {
        return new DocumentEntity(title, "Test author", DocumentFileType.PDF, "test.pdf", 10);
    }

    private DocumentChunkEntity chunk(DocumentEntity document, int index, String text, float[] vector) {
        var chunk = new DocumentChunkEntity(document, index, text, 2, 3, "Test section", 2, vector);
        document.addChunk(chunk);
        return chunk;
    }

    private float[] vector(float x, float y) {
        float[] vector = new float[768];
        vector[0] = x;
        vector[1] = y;
        return vector;
    }
}
