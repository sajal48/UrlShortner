## URL Shortener Application

### Starting the Application

1.  Navigate to the `docker-composes` folder.
2.  Run the following command to start the Docker containers:

    ```sh
    docker compose -f .\docker-composer.yml up -d
    ```
3.  Wait for a minute to ensure all services are up and running.
4.  Start both applications.

### Endpoints

#### Shorten URL

* **URL:** `/api/v1/url`
* **Method:** `POST`
* **Consumes:** `application/json`
* **Produces:** `application/json`
* **Request Body:**

    ```json
    {
      "longUrl": "https://example.com"
    }
    ```
* **Response Body:**

    ```json
    {
      "longUrl": "https://example.com",
      "shortUrl": "http://short.url/abc123"
    }
    ```

#### Get Long URL

* **URL:** `/api/v1/url/{shortUrl}`
* **Method:** `GET`
* **Path Variable:** `shortUrl` (e.g., `abc123`)
* **Response:** Redirects to the original long URL

### Monitoring and Tracing

Now you can:

* View distributed traces in the **Zipkin UI**: [http://localhost:9411](http://localhost:9411)
* Monitor metrics in **Prometheus**: [http://localhost:9090](http://localhost:9090)
* Create custom dashboards in **Grafana**: [http://localhost:3000](http://localhost:3000)
    * **Note:** When adding Prometheus as a data source in Grafana, use the URL: `http://host.docker.internal:9090`

### Prometheus Configuration

Prometheus is configured to scrape metrics from the Spring Boot application and itself. The configuration is defined in the `prometheus.yml` file.