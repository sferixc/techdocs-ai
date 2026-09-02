import {useEffect, useState} from "react";
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

type Querystring = {
    query: string;
}

function App(){
    const[documents, setDocuments] = useState<DocumentSearchResult[]>([]);
    const[queryString, setQueryString] = useState('');
    const [searching, setSearching] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function loadDocuments() {
        try{
            const response = await fetch(
                'http://localhost:8080/search',
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



        </main>
    );
}

export default App