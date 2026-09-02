import {useState} from "react";
import './App.css';

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
    const[queryString, setQueryString] = useState('');
    const [searching, setSearching] = useState(false);
    const [error, setError] = useState<string | null>(null);



    async function loadDocuments() {

        setSearching(true);
        setError(null);
        setDocuments([]);

        try{
            const response = await fetch(
                'http://localhost:8080/api/documents/search',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        query: queryString.trim(),
                    })
                });

            if(!response.ok) throw new Error(
                `HTTP error! status: ${response.status}`
            )

            const data = await response.json();

            setDocuments(data);
        }
        catch (error) {
            setError(
                error instanceof Error ? error.message : "Search failed."
            );
        } finally {
            setSearching(false);
        }

    }

    return (
        <main>
            <h1>TechDocs AI</h1>

            <form onSubmit={(e) => {
                e.preventDefault();

                loadDocuments();
            }}>

                <input placeholder = "search" type = "text"
                value = {queryString}
                onChange = {(e) => setQueryString(e.target.value)}>
                </input>

                <button type = "submit" disabled = {searching || !queryString.trim()}>
                    {searching ? "Searching..." : "Search"}
                </button>

            </form>

            {error && <p>{error}</p>}

            {documents.length > 0 && (documents.map((document) => (
                <div key = {document.chunkId}>
                    <h2>{document.documentTitle}</h2>
                    <p>{document.content}</p>
                </div>
            )))}


        </main>
    );
}

export default App