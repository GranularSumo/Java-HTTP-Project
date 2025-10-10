# Java HTTP/1.1 Server

A basic HTTP/1.1 server implementation in Java, built as a learning exercise by translating Boot.dev's [Learn the HTTP Protocol in Go](https://www.boot.dev/courses/learn-http-protocol-golang) course from Go to Java.

## Features

- **HTTP/1.1 Protocol Support**
  - Request line parsing with method validation
  - Header parsing with duplicate header handling
  - Request body parsing with Content-Length
  - CRLF handling and incremental buffering

- **Response Writing**
  - Standard HTTP responses (HTML, CSS, JavaScript)
  - Chunked transfer encoding with trailers
  - Can serve binary data (Images, Videos, etc.)
  - Custom error pages

- **Server Capabilities**
  - Concurrent request handling with thread pool
  - Configurable socket timeouts
  - Reverse proxy functionality with chunked responses
  - Static file serving

- **Handler System**
  - Basic Handler interface
  - Default handler for static content
  - Proxy handler with SHA-256 trailer support
  - Video serving handler

## Quick Start

### Prerequisites
- Java 11 or higher
- Gradle 8.x

### Running the Server

```bash
./gradlew run
```

The server will start on `http://localhost:9001/`

## Example Endpoints

- `GET /` - Serves the main HTML page with CSS
- `GET /video` - serves some video content (MP4)
- `GET /jsonplaceholder/*` - Proxies requests to JSONPlaceholder API
- `POST /*` - Returns 400 Bad Request
- Any other path - Returns 404 Not Found

## Things I would like to add in the future
- Implement other HTTP features like compression, keep-alive, etc.
- Look into implementing SSE
- Work on the project structure and make the Handlers easier to use and more reusable.
- Work out how to handle file paths for resource hosting in a nicer way.

