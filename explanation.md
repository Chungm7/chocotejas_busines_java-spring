Here is a step-by-step explanation of the changes I made to fix the client module:

1.  **Created DTOs for API Responses:** I created `RucApiResponse.java` and `DniApiResponse.java` to represent the JSON responses from the external API. This makes the code more robust and easier to maintain.

2.  **Refactored `ExternalApiServiceImpl`:** I replaced the synchronous `RestTemplate` with the modern, non-blocking `WebClient` for making HTTP requests. This improves performance and error handling. I also used the new DTOs to parse the API responses.

3.  **Updated `ClienteServiceImpl`:** I updated the `buscarOCrearCliente` method to work with the new reactive `ExternalApiServiceImpl` and DTOs. I also ensured that the client's address is not automatically populated from the API response, as you requested.

4.  **Refactored `gestion-clientes.js`:** I removed the unused and insecure hardcoded API token from the frontend JavaScript. I also improved the user experience by adding loading indicators and more specific notifications.

5.  **Fixed a Bug in `PerfilRepository`:** I fixed a bug in the `PerfilRepository` that was causing the tests to fail. The `findByEstadoTrue()` method was replaced with `findByEstado(1)` to correctly query for active profiles.

6.  **Configured the Test Environment:** I created a test-specific `application.properties` file to ensure that the tests run in a clean and consistent environment, using an in-memory H2 database.

7.  **Improved Project Configuration:** I added `src/main/resources/application.properties` to the `.gitignore` file to prevent sensitive information from being committed to the repository. I also removed the `target/`, `app.log`, and `jules-scratch/` directories from the repository.
