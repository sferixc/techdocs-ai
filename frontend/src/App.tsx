import {useState} from "react";
import './App.css';

const API_URL = 'http://localhost:8080/api/documents';

const suggestedQueries = ['How does late interaction improve retrieval?',
    'How can language models use external knowledge?',
    'How does contrastive learning improve sentence embeddings?',];

type DocumentSummary = {
    documentId: number;
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
    const[suggestedQuery, setSuggestedQuery] = useState('');
    const [searching, setSearching] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function loadDocumentSummaries(){

        try{
            const response = await fetch(API_URL);
            if(!response.ok) throw new Error(
                `HTTP error! status: ${response.status}`
            )
            const data = await response.json();
            setDocumentSummaries(data);
        }
        catch(Error error){
            if(error instanceof Error){
                setError(error.message);
            }
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

            setDocuments(data);
        }
        catch (error) {
            setDocuments([]);
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
                            {searching ? <span className = "spinner"/> : "Search">}
                        </button>
                    </form>

                    <div>
                        <span>Try</span>
                        {suggestedQueries.map((suggestion) => (
                            <button key = {suggestion} onClick={handleSuggestionSearch(suggestion)}>{suggestion}</button>
                        ))}
                    </div>

                </section>

                {error && <div className="error-message" role="alert">{error}</div>}





            </main>
        </div>
    );
}

export default App