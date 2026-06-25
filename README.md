## 🚀 Quick Start (How to Build and Run Locally)


### 🚀 Prerequisites
To get this application running on your local machine, you will need:
* **Docker:** Required for running the containerized application and the PostgreSQL database.
* **JDK 17 or above:** Required if you wish to run the test suite or build the application locally using Maven. *(Note: This project has been successfully tested with both JDK 17 and JDK 21).*
* **Maven:** Required for running the local test suite and building the project. *(Note: This project was built and tested using Maven 3.9.16).*

### Running Tests
This project includes both unit tests and integration tests. Because the integration tests interact with a real PostgreSQL database, the database must be running before you execute the test suite.

1. Start the database (e.g., `docker-compose up -d db`)
2. Run the tests: `mvn clean test`
3. Wait for the tests to complete.

### Starting the Application

**Step 1: Start the Application**
Navigate to the project root directory in your terminal and run the following command to build and start the containers:

```bash
docker compose up --build
```
**Step 2: Wait for Initialization** 

The application will take a moment to download dependencies and initialize the database. Wait until you see the following line in your terminal's log output, which confirms the backend is ready:
main] o.s.boot.tomcat.TomcatWebServer : Tomcat started on port 8080 (http) with context path '/' <br>

💻 Example Usage
##### Frontend UI
Once the backend is successfully running, open your web browser and navigate to the decoupled React frontend at:
👉 http://localhost:5173

##### Shortening a URL 
You will be presented with a user interface containing a form with the following fields:
* Full URL: (Input field for the original long URL)
* Custom Alias (Optional): (Input field for your preferred custom alias) 
* [ Shorten ] (A shorten button to submit the request)

Once you press the [ Shorten ] button, the system will generate a shortened URL for you. <br> If you provided a custom alias, it will be used; otherwise, a random alias will be generated.
##### Viewing Your URLs 
Directly below this form, you will find a list titled All Shortened URLs. This section will dynamically display the URLs you have successfully shortened and saved to the database.

**API Usage**

You can also interact directly with the REST API. Here is an example of shortening a URL via the terminal:
```bash
curl -X POST http://localhost:8080/shorten \
-H "Content-Type: application/json" \
-d '{"fullUrl": "https://example.com/very/long/url", "customAlias": "my-custom-alias"}'
```


##### 💡 Assumptions
**Error Responses:** 
There is no response body specified in the openapi.yaml contract for when an alias does not exist (404 Not Found). I was tempted to add a JSON error text message for clarity, but I deliberately left it empty to strictly conform with the provided OpenAPI specification


# URL Shortener Coding Exercise

## Task

Build a simple **URL shortener** in **your preferred language** (e.g. Java, C#, Python).

It should:

- Accept a full URL and return a shortened URL.
- A shortened URL should have a randomly generated alias.
- Allow a user to **customise the shortened URL** if they want to (e.g. user provides `my-custom-alias` instead of a random string).
- Persist the shortened URLs across restarts.
- Expose a **decoupled web frontend** built with a modern framework (e.g., React, Next.js, Vue.js, Angular, Flask with templates). This can be lightweight form/output just to demonstrate interaction with the API. Feel free to use UI frameworks like Bootstrap, Material-UI, Tailwind CSS, GOV.UK design system, etc. to speed up development.
- Expose a **RESTful API** to perform create/read/delete operations on URLs.  
  → Refer to the provided [`openapi.yaml`](./openapi.yaml) for API structure and expected behaviour.
- Include the ability to **delete a shortened URL** via the API.
- **Have tests**.
- Be containerised (e.g. Docker).
- Include instructions for running locally.

## Rules

- Fork the repository and work in your fork. Do not push directly to the main repository.
- There is no time limit, we want to see something you are proud of. We would like to understand roughly how long you spent on it though.
- **Commit often with meaningful messages.**
- Write tests.
- The API should validate inputs and handle errors gracefully.
- The Frontend should show errors from the API appropriately.
- Use the provided [`openapi.yaml`](./openapi.yaml) as the API contract.
- Focus on clean, maintainable code.
- AI tools (e.g., GitHub Copilot, ChatGPT) are allowed, but please **do not** copy-paste large chunks of code. Use them as assistants, not as a replacement for your own work. We will be asking.

## Deliverables

- Working software.
- Decoupled web frontend (using a modern framework like React, Next.js, Vue.js, Angular, or Flask with templates).
- RESTful API matching the OpenAPI spec.
- Tests.
- A git commit history that shows your thought process.
- Dockerfile.
- README with:
  - How to build and run locally.
  - Example usage (frontend and API).
  - Any notes or assumptions.
