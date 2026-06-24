import {useState} from 'react';
import {useForm} from 'react-hook-form';
import axios from 'axios';

function App() {
    // Initialize our form tools
    const {register, handleSubmit, setError, formState: {errors}} = useForm();

    // Initialize our state
    const [shortUrl, setShortUrl] = useState('');


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
                if(!error.response) {
                    setError('apiError', {
                        message: 'An unexpected error occurred or network error: Cannot connect to the backend. Is back-end server running?'
                    });
                }
                else {
                    setError('apiError', {
                        message: error.response?.data?.message || 'Alias already taken or invalid input'
                    });
                }
            });
    };

    // 4. Render the UI with the form
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

            {/* 5. Display API Errors */}
            {errors.apiError && <p style={{ color: 'red', marginTop: '1rem' }}>{errors.apiError.message}</p>}
            {/* 6. Display Success */}
            {shortUrl && (
                <div style={{ marginTop: '1rem', color: 'green' }}>
                    Success! Your short URL is: <a href={shortUrl} target="_blank" rel="noreferrer">{shortUrl}</a>
                </div>
            )}
        </div>
    );
}

export default App;
