package com.eric.techdocsai.document;

import com.eric.techdocsai.embedding.OllamaEmbeddingClient;
import com.eric.techdocsai.embedding.OllamaEmbeddingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentSearchServiceTest {
    @Mock DocumentChunkRepository repository;
    @Mock OllamaEmbeddingClient client;
    private DocumentSearchService service;

    @BeforeEach
    void setUp() {
        service = new DocumentSearchService(repository, client);
    }

    @Test
    void embedsQueryAndMapsMatchesInRepositoryOrder() {
        when(client.createEmbedding("What is overfitting?"))
                .thenReturn(new OllamaEmbeddingResponse(new float[][]{{1f, 0.5f, -0.25f}}));
		var closest = match(12L, 0.2);
		var next = match(19L, 0.6);
        when(repository.findClosestChunks("[1.0, 0.5, -0.25]"))
                .thenReturn(List.of(closest, next));

        var results = service.search("What is overfitting?");

        assertEquals(2, results.size());
        var first = results.getFirst();
        assertAll(
                () -> assertEquals(1L, first.documentId()),
                () -> assertEquals("Useful ML", first.documentTitle()),
                () -> assertEquals(12L, first.chunkId()),
                () -> assertEquals("Overfitting reduces generalization.", first.content()),
                () -> assertEquals(2, first.startPage()),
                () -> assertEquals(3, first.endPage()),
                () -> assertEquals(0.8, first.similarity(), 1e-9),
                () -> assertEquals(19L, results.get(1).chunkId()),
                () -> assertEquals(0.4, results.get(1).similarity(), 1e-9)
        );
        verify(client).createEmbedding("What is overfitting?");
        verify(repository).findClosestChunks("[1.0, 0.5, -0.25]");
    }

    @Test
    void returnsEmptyListWhenNothingMatches() {
        when(client.createEmbedding("query"))
                .thenReturn(new OllamaEmbeddingResponse(new float[][]{{1f, 0f}}));
        when(repository.findClosestChunks("[1.0, 0.0]")).thenReturn(List.of());

        assertTrue(service.search("query").isEmpty());
    }

    @Test
    void doesNotQueryDatabaseWhenOllamaFails() {
        var failure = new IllegalStateException("Ollama unavailable");
        when(client.createEmbedding("query")).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> service.search("query")));
        verifyNoInteractions(repository);
    }

    private ChunkSearchMatch match(Long id, double distance) {
        var match = mock(ChunkSearchMatch.class);
        when(match.getChunkId()).thenReturn(id);
        when(match.getDocumentId()).thenReturn(1L);
        when(match.getDocumentTitle()).thenReturn("Useful ML");
        when(match.getContent()).thenReturn("Overfitting reduces generalization.");
        when(match.getStartPage()).thenReturn(2);
        when(match.getEndPage()).thenReturn(3);
        when(match.getDistance()).thenReturn(distance);
        return match;
    }
}
