import org.example.chunkReader.ChunkReader;
import org.example.request.Request;
import org.example.request.RequestParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTests {

    @Test
    void TestGoodRequestLine() {
        String raw =
                "GET / HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("GET", request.getRequestLine().method());
        assertEquals("/", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
    }

    @Test
    void TestGoodRequestLineWithPath() {
        String raw =
                "GET /coffee HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 1);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("GET", request.getRequestLine().method());
        assertEquals("/coffee", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
    }

    @Test
    void TestGoodPOSTRequestWithPath() {
        String raw =
                "POST /coffee HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 200);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("POST", request.getRequestLine().method());
        assertEquals("/coffee", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
    }

    @Test
    void TestGoodOPTIONSRequestTarget() {
        String raw =
                "OPTIONS * HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("OPTIONS", request.getRequestLine().method());
        assertEquals("*", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
    }

    @Test
    void TestRequestWithGoodHeaders() {
        String data =
                "GET / HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(data, 3);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);
        assertEquals("localhost:9001", request.getHeaders().getValue("host"));
        assertEquals("curl/7.81.0", request.getHeaders().getValue("user-agent"));
        assertEquals("*/*", request.getHeaders().getValue("accept"));
    }

    @Test
    void TestInvalidNumPartsInRequestLine() {
        String raw =
                "/coffee HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

    @Test
    void TestInvalidRequestLineOrder() {
        String raw =
                "/coffee GET HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

    @Test
    void TestInvalidHTTPVersion() {
        String raw =
                "GET /coffee HTTP/1.0\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

    @Test
    void TestInvalidGETRequestTarget() {
        String raw =
                "GET coffee HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

    @Test
    void TestInvalidOPTIONSRequestTarget() {
        String raw =
                "OPTIONS coffee HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

    @Test
    void TestInvalidNumBytesInChunkReader() {
        String raw =
                "OPTIONS coffee HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        assertThrows(IllegalArgumentException.class, () -> new ChunkReader(raw, -1));
    }

    @Test
    void TestMalformedHeader() {
        String raw =
                "GET / HTTP/1.1\r\n" +
                        "Host localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

    @Test
    void TestEmptyHeaders() {
        String raw = "GET / HTTP/1.1\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("GET", request.getRequestLine().method());
        assertEquals("/", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
    }

    @Test
    void TestDuplicateHeaders() {
        String raw = "GET / HTTP/1.1\r\n test: test1\r\n test: test2\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("GET", request.getRequestLine().method());
        assertEquals("/", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
        assertEquals("test1, test2", request.getHeaders().getValue("test"));
    }

    @Test
    public void TestCaseInsensitiveHeaders() {
        String raw = "GET / HTTP/1.1\r\n TeSt: success\r\n\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(reader));
        assertNotNull(request);

        assertEquals("GET", request.getRequestLine().method());
        assertEquals("/", request.getRequestLine().requestTarget());
        assertEquals("1.1", request.getRequestLine().httpVersion());
        assertEquals("success", request.getHeaders().getValue("test"));
    }

    @Test
    public void TestMissingEndOfHeaders() {
        String raw = "GET / HTTP/1.1\r\n TeSt: success\r\n";

        ChunkReader reader = new ChunkReader(raw, 3);
        assertThrows(IOException.class, () -> RequestParser.requestFromReader(reader));
    }

}
