package com.eric.techdocsai.document;

public interface ChunkSearchMatch {
	Long getChunkId();
	Long getDocumentId();
	String getDocumentTitle();
	String getContent();
	Integer getStartPage();
	Integer getEndPage();
	Double getDistance();
}