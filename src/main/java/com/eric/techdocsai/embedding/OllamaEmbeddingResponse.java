package com.eric.techdocsai.embedding;
import java.util.*;

public record OllamaEmbeddingResponse(
		List<List<Double>> embeddings
) {
}
