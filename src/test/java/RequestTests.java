import org.example.request.Request;
import org.example.request.RequestParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTests {

    @Test
    void TestGoodRequestLine() {
        String raw =
                "GET / HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(new StringReader(raw)));
        assertNotNull(request);

        assertEquals("GET", request.requestLine().method());
        assertEquals("/", request.requestLine().requestTarget());
        assertEquals("1.1", request.requestLine().httpVersion());
    }

    @Test
    void TestGoodRequestLineWithPath() {
        String raw =
                "GET /coffee HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(new StringReader(raw)));
        assertNotNull(request);

        assertEquals("GET", request.requestLine().method());
        assertEquals("/coffee", request.requestLine().requestTarget());
        assertEquals("1.1", request.requestLine().httpVersion());
    }

    @Test
    void TestGoodPOSTRequestWithPath() {
        String raw =
                "POST /coffee HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(new StringReader(raw)));
        assertNotNull(request);

        assertEquals("POST", request.requestLine().method());
        assertEquals("/coffee", request.requestLine().requestTarget());
        assertEquals("1.1", request.requestLine().httpVersion());
    }

    @Test
    void TestGoodOPTIONSRequestTarget() {
        String raw =
                "OPTIONS * HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        Request request = assertDoesNotThrow(() -> RequestParser.requestFromReader(new StringReader(raw)));
        assertNotNull(request);

        assertEquals("OPTIONS", request.requestLine().method());
        assertEquals("*", request.requestLine().requestTarget());
        assertEquals("1.1", request.requestLine().httpVersion());
    }

    @Test
    void TestInvalidNumPartsInRequestLine() {
        String raw =
                "/coffee HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        assertThrows(IOException.class, () -> RequestParser.requestFromReader(new StringReader(raw)));
    }

    @Test
    void TestInvalidRequestLineOrder() {
        String raw =
                "/coffee GET HTTP/1.1\r\n" +
                        "Host: localhost:9001\r\n" +
                        "User-Agent: curl/7.81.0\r\n" +
                        "Accept: */*\r\n\r\n";

        assertThrows(IOException.class, () -> RequestParser.requestFromReader(new StringReader(raw)));
    }

    @Test
    void TestInvalidHTTPVersion() {
        String raw =
                "GET /coffee HTTP/1.0\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        assertThrows(IOException.class, () -> RequestParser.requestFromReader(new StringReader(raw)));
    }

    @Test
    void TestInvalidGETRequestTarget() {
        String raw =
                "GET coffee HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        assertThrows(IOException.class, () -> RequestParser.requestFromReader(new StringReader(raw)));
    }

    @Test
    void TestInvalidOPTIONSRequestTarget() {
        String raw =
                "OPTIONS coffee HTTP/1.1\r\n" +
                "Host: localhost:9001\r\n" +
                "User-Agent: curl/7.81.0\r\n" +
                "Accept: */*\r\n\r\n";

        assertThrows(IOException.class, () -> RequestParser.requestFromReader(new StringReader(raw)));
    }
}
