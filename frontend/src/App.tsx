import {useState, useEffect} from "react";
import './App.css';

const API_URL = 'http://localhost:8080/api/documents';

const suggestedQueries = ['How does late interaction improve retrieval?',
    'How can language models use external knowledge?',
    'How does contrastive learning improve sentence embeddings?',];

function pageLabel(startPage: number | null, endPage: number | null) {
    if (startPage === null) return 'Page unavailable';
    if (endPage === null || startPage === endPage) return `Page ${startPage}`;
    return `Pages ${startPage}–${endPage}`;
}

type DocumentSummary = {
    documentId: number;
    wordCount: number;
    fileType: string;
    author: string;
    documentTitle: string;
    chunkId: number;
    content: string;
}

type DocumentSearchResult = {
    documentId: number;
    documentTitle: string;
    chunkId: number;
    content: string;
    startPage: number | null;
    endPage: number | null;
    similarity: number;
};

// type queryString = {
//     query: string;
// }

function App(){
    const[documents, setDocuments] = useState<DocumentSearchResult[]>([]);
    const[documentSummaries, setDocumentSummaries] = useState<DocumentSummary[]>([]);
    const[queryString, setQueryString] = useState('');
    const[submittedQuery, setSubmittedQuery] = useState('');
    const [searching, setSearching] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadDocumentSummaries();
    }, [])

    async function loadDocumentSummaries(){

        try{
            const response = await fetch(API_URL);
            if(!response.ok)(
                `HTTP error! status: ${response.status}`
            )
            const data = await response.json();
            setDocumentSummaries(data);
        }
        catch {
            setError("Failed to load document summaries.");
        }

    }

    async function search(query: string) {

        setSearching(true);
        setError(null);
        setDocuments([]);

        try{
            const response = await fetch(
                `${API_URL}/search`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        query: query.trim(),
                    })
                });

            if(!response.ok) throw new Error(
                `HTTP error! status: ${response.status}`
            )

            const data = await response.json();

            setSubmittedQuery(query);
            setDocuments(data);
        }
        catch (error) {
            setDocuments([]);
            setSubmittedQuery('');
            setError(
                error instanceof Error ? error.message : "Search failed."
            );
        } finally {
            setSearching(false);
        }

    }

    async function handleNormalSearch(event: React.FormEvent){
        event.preventDefault();
        await search(queryString);
    }

    async function handleSuggestionSearch(suggestion: string){
        await search(suggestion);
        setQueryString(suggestion);
    }

    return (
        <div className = "app-shell">

            <header className = "header">
                <div className = "brandmark"><div className = "logo">
                    <img src = "logo.png" alt = "logo" />
                </div>

                </div>
            </header>

            <main>
                <section  className="search-section" aria-labelledby="search-title">
                    <p className="eyebrow">Semantic research search</p>
                    <h1 id="search-title">Find the idea, not just the keyword.</h1>
                    <p className="intro">Search foundational papers on retrieval, embeddings and retrieval-augmented generation.</p>\

                    <form className="search-form" onSubmit={handleNormalSearch}>
                        <span className="search-icon" aria-hidden="true">⌕</span>
                        <input placeholder = "Search for papers..." onChange={(e) => {
                            setQueryString(e.target.value);
                        }}/>
                        <button type = "submit" disabled = {searching || !queryString.trim()}>
                            {searching ? <span className = "spinner"/> : "Search"}
                        </button>
                    </form>

                    <div>
                        <span>Try</span>
                        {suggestedQueries.map((suggestion) => (
                            <button key = {suggestion} onClick={() => handleSuggestionSearch(suggestion)}>{suggestion}</button>
                        ))}
                    </div>

                </section>

                {error && <div className="error-message" role="alert">{error}</div>}

                {searching
                    && <section className="results-section" aria-label="Searching">
                    <div className="section-heading">
                        <h2>Searching the library…</h2>
                    </div>{[1, 2, 3].map((item) => <div className="result-card skeleton" key={item} />)}
                </section>
                }

                {!searching && documents.length > 0 && (
                    <section className="results-section">
                        <div className="section-heading"><div><p className="eyebrow">Top passages</p><h2 id="results-title">Results for “{submittedQuery}”</h2></div><span>{documents.length} matches</span></div>
                        <div className="results-list">
                            {documents.map((result, index) => (
                                <article className="result-card" key={result.chunkId}>
                                    <div className="result-rank">0{index + 1}</div>
                                    <div className="result-content">
                                        <div className="result-meta"><span>{pageLabel(result.startPage, result.endPage)}</span><span className="match-score">{Math.round(result.similarity * 100)}% match</span></div>
                                        <h3>{result.documentTitle}</h3>
                                        <p>{result.content}</p>
                                    </div>
                                </article>
                            ))}
                            {documents.length === 0 && !error && <div className="empty-state">No relevant passages were found. Try a broader question.</div>}
                        </div>
                    </section>
                )}

                {!submittedQuery && (
                    <section className = "library-section">
                        <div className = "section-heading">
                            <div>
                                <p className = "eyebrow">Library</p>
                                <h2>Explore the library</h2>
                            </div>
                            <span>{documentSummaries.length} documents</span>
                        </div>

                        <div className = "document-grid">
                            {documentSummaries.map((document, index) => (
                                <article className="document-card" key={document.documentId}>
                                    <div className={`paper-icon tone-${index % 4}`} aria-hidden="true"><span>PDF</span></div>
                                    <div><p className="document-kind">Research paper</p><h3>{document.documentTitle}</h3><p className="document-author">{document.author === 'Unknown' ? 'Machine learning research' : document.author}</p></div>
                                    <div className="document-footer"><span>{document.wordCount.toLocaleString()} words</span><span>{document.fileType}</span></div>
                                </article>
                            ))}
                        </div>
                    </section>
                )}

            </main>
        </div>
    );
}

export default App