# TechDocs AI

An application for semantic search across technical documentation and PDF papers. Users ask a question, and the application finds passages with related meaning, showing the source document, text, and associated pages.

The project is under development. The current version implements text extraction, chunking, embedding generation, storage, and vector search, along with an interface for browsing the library and viewing results.

## Implemented features

- Document library displaying titles, authors, file types, and word counts.
- Page-by-page PDF text extraction using Apache PDFBox.
- Sentence-based chunking targeting 250 words per chunk, with start and end pages preserved.
- Local embedding generation with `embeddinggemma` through Ollama.
- Document, chunk, and vector persistence in PostgreSQL with pgvector.
- Semantic search using cosine similarity.
- Search results displaying the document title, passage, page range, and similarity score.
- React interface with suggested questions, loading indicators, and error messages.
- Tests for PDF parsing, the search service, the search endpoint, and the vector repository.

The application returns passages from documents; it does not currently generate conversational answers from them.

## Technology stack

| Component | Technologies |
| --- | --- |
| Backend | Java 21, Spring Boot, Spring Web MVC, Spring Data JPA |
| PDF processing | Apache PDFBox |
| Embeddings | Ollama, `embeddinggemma` |
| Database | PostgreSQL 16, pgvector, Hibernate Vector |
| Frontend | React, TypeScript, Vite, CSS |
| Testing | JUnit, Mockito, MockMvc, Testcontainers |
| Local environment | Docker Compose, Maven Wrapper, npm |

## How it works

During indexing:

```text
PDF → text by page → chunks → embeddings through Ollama → PostgreSQL
```

During search:

```text
Question → embedding through Ollama → comparison in pgvector → passages in the UI
```

The backend currently retrieves the 3 closest chunks across the entire collection. The score is calculated as `1 - cosine distance`; the percentage displayed in the interface is not a probability of correctness. There is no minimum relevance threshold or reranking step yet.

The current chunker does not use overlap. Page text is joined and whitespace is normalized before splitting it into sentences. A chunk can span multiple pages, and a very long sentence can exceed the 250-word target.

## Running locally

You need JDK 21, Docker with Docker Compose, Ollama, and a Node.js/npm version compatible with the Vite dependency in `frontend/package.json`. The commands below use PowerShell and run from the project root unless stated otherwise.

### 1. Database

```powershell
docker compose up -d
```

Once PostgreSQL is ready, enable the vector extension in the application database:

```powershell
docker compose exec postgres psql -U techdocs -d techdocs_ai -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

The local configuration in `docker-compose.yml` and `application.properties` uses the `techdocs_ai` database, with `techdocs` as both username and password, on port `5433`. Data persists in the `techdocs_postgres_data` Docker volume.

### 2. Ollama

With the Ollama service running, download the model:

```powershell
ollama pull embeddinggemma
```

The backend client currently uses `http://localhost:11434` and the `/api/embed` endpoint, with the model name defined in code.

### 3. Backend

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080` by default.

On startup, documents are parsed and indexed in the database.

### 4. Frontend

In a separate terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The frontend calls the API at `http://localhost:8080/api/documents`; the CORS configuration allows the local origin `http://localhost:5173`.

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/documents` | Lists documents, newest first |
| `POST` | `/api/documents/search` | Searches for passages relevant to a question |

Example search from PowerShell:

```powershell
Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:8080/api/documents/search' `
  -ContentType 'application/json' `
  -Body '{"query":"What is overfitting?"}'
```

Each result contains `documentId`, `documentTitle`, `chunkId`, `content`, `startPage`, `endPage`, and `similarity`. Page numbers refer to positions in the PDF file, starting at 1.

## Project structure

```text
frontend/                    React interface and Vite configuration
src/main/java/.../
  document/                  Entities, PDF parsing, chunking, indexing, and search
  embedding/                 Ollama integration and embedding data structures
  config/                    Web configuration
src/main/resources/          Application configuration and bundled PDFs
src/test/                    Tests and pgvector initialization for tests
docker-compose.yml           PostgreSQL with pgvector for local development
pom.xml                      Backend dependencies and build configuration
```

## Verification

To run the backend tests:

```powershell
.\mvnw.cmd test
```

Repository tests start a separate PostgreSQL instance through Testcontainers and require Docker. The application context test uses the application configuration and requires the local database; if it is empty, the seeder also needs Ollama for indexing.

To build and lint the frontend, run these commands from the `frontend` directory:

```powershell
npm run build
npm run lint
```

These commands describe the available checks, not the outcome of their latest execution.

## Next steps

The next stage is opening PDFs from the library and search results through an endpoint that serves the document's associated file. Results will be grouped by PDF, with access to multiple relevant passages and links to their pages.

Search improvements will focus on better ranking using existing models: a small evaluation set, text extraction checks, experiments with chunk size and overlap, combining semantic and lexical search, and evaluating a reranker. Document scoring will account for distinct passages without artificially favoring repetition or long documents. Training a custom model is not planned at this stage.

Visual improvements, including a Home button and search reset without a page reload, will follow gradually, with verification after each step.
