import {useEffect, useState} from "react";
import './App.css';

type DocumentSummary = {
    id: number;
    title: string;
    author: string;
    fileType: string;
    sourcePath: string;
    wordCount: number;
    createdAt: string;
};

function App(){
    const[documents, setDocuments] = useState<DocumentSummary[]>([]);
    const[loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        async function loadDocuments() {
            const response = await fetch('http://localhost:8080/api/documents');
            const data = await response.json();

            await new Promise(resolve => setTimeout(resolve, 2000));

            setLoading(false);
            setDocuments(data);

        }

        loadDocuments();

    }, []);

    return (
        <main>
            <h1>TechDocs AI</h1>

            <h2>Preloaded documents</h2>

            {loading && <p>Loading documents...</p>}

            {!loading && documents.length === 0 && (
                <p>No documents found.</p>
            )}

            {!loading && documents.map((document) => (
                <article key={document.id}>
                    <h3>{document.title}</h3>
                    <p>Author: {document.author}</p>
                    <p>Type: {document.fileType}</p>
                    <p>Words: {document.wordCount}</p>
                    <p>Source: {document.sourcePath}</p>
                </article>
            ))}
        </main>
    );
}

export default App