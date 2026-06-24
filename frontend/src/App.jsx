import {useEffect, useState} from 'react';
import {useForm} from 'react-hook-form';
import axios from 'axios';

function App() {
    // Initialize our form tools
    const {register, handleSubmit, setError, formState: {errors}} = useForm();

    // Initialize our state
    const [shortUrl, setShortUrl] = useState('');
    const [urls, setUrls] = useState([]);

    // Create a function to fetch all URLs from the backend
    const fetchUrls = () => {
        axios.get('http://localhost:8080/urls')
            .then(response => {
                setUrls(response.data); // Save the array of URLs into our state
            })
            .catch(error => {
                console.error("Could not fetch URLs. Is the GET endpoint built?", error);
            });
    };

    const handleDelete = (alias) => {
        // Call the DELETE /{alias} endpoint
        axios.delete(`http://localhost:8080/${alias}`)
            .then(() => {
                // If successful (204 No Content), refresh the table to remove the row
                fetchUrls();
            })
            .catch(error => {
                console.error("Could not delete URL:", error);
                alert("Could not delete the URL.");
            });
    };

    // Use 'useEffect' to run the fetch function once when the page first loads
    useEffect(() => {
        fetchUrls();
    }, []);


    // Create the function that runs when the form is submitted
    const onSubmit = (data) => {
        // console.log("Form data submitted:", data);
        // We will add the API call here in Part 3!
        setShortUrl('');

        // Call your Spring Boot API
        axios.post('http://localhost:8080/shorten', data)
            .then(response => {
                // Success: display the shortened URL
                setShortUrl(response.data.shortUrl);
            })
            .catch(error => {
                // Failure: capture the error from the API request
                if (error.response) {
                    setError('apiError', {
                        message: error.response?.data?.message || 'Alias already taken or invalid input'
                    });
                } else {
                    setError('apiError', {
                        message: 'An unexpected error occurred or network error: Cannot connect to the backend. Is back-end server running?'
                    });
                }
            });
    };

    // Render the UI with the form
    return (
        <div style={{padding: '2rem', fontFamily: 'sans-serif'}}>
            <h1>URL Shortener</h1>

            <form onSubmit={handleSubmit(onSubmit)}>
                <div>
                    <label>Full URL: </label>
                    {/* The register function connects this input to our form tools */}
                    <input {...register('fullUrl', {required: true})} type="url" placeholder="https://example.com"/>
                </div>

                <div style={{marginTop: '1rem'}}>
                    <label>Custom Alias (Optional): </label>
                    <input {...register('customAlias')} type="text" placeholder="my-alias"/>
                </div>

                <button type="submit" style={{marginTop: '1rem'}}>Shorten</button>
            </form>

            {/* Display API Errors */}
            {errors.apiError && <p style={{color: 'red', marginTop: '1rem'}}>{errors.apiError.message}</p>}
            {/* Display Success */}
            {shortUrl && (
                <div style={{marginTop: '1rem', color: 'green'}}>
                    Success! Your short URL is: <a href={shortUrl} target="_blank" rel="noreferrer">{shortUrl}</a>
                </div>
            )}

            {/* Draw the Table */}
            <hr style={{margin: '2rem 0'}}/>
            <h2>All Shortened URLs</h2>

            <table style={{width: '100%', textAlign: 'left', borderCollapse: 'collapse'}}>
                <thead>
                <tr style={{borderBottom: '2px solid #ccc'}}>
                    <th>Alias</th>
                    <th>Original URL</th>
                    <th>Short URL</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {/* We use .map() to loop through the 'urls' array and create a row for each one */}
                {urls.map((url) => (
                    <tr key={url.alias} style={{borderBottom: '1px solid #eee'}}>
                        <td style={{padding: '0.5rem 0'}}>{url.alias}</td>
                        <td><a href={url.fullUrl} target="_blank" rel="noreferrer" style={{color: 'blue'}}>Link</a></td>
                        <td><a href={url.shortUrl} target="_blank" rel="noreferrer"
                               style={{color: 'blue'}}>{url.shortUrl}</a></td>
                        <td>
                            <button
                                onClick={() => handleDelete(url.alias)}
                                style={{color: 'red', cursor: 'pointer'}}>
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default App;
