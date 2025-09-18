import org.example.headers.Headers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HeadersTests {

    @Test
    public void TestValidSingleHeader() throws IOException {
        Headers headers = new Headers();
        String data = "Host: localhost:9001\r\n\r\n";

        headers.parse(data.getBytes());
        assertEquals("localhost:9001", headers.getHeaderMap().get("host"));
    }

    @Test
    public void TestValidSingleHeaderWithExtraWhitespaces() throws IOException {
        Headers headers = new Headers();
        String data = "    Host:        localhost:9001         \r\n\r\n";

        headers.parse(data.getBytes());
        assertEquals("localhost:9001", headers.getHeaderMap().get("host"));
    }

    @Test
    public void Test2ValidHeaders() throws IOException {
        Headers headers = new Headers();
        String data = "Host: localhost:9001\r\n";
        String data2 = "Test: anotherTestHeader\r\n\r\n";

        headers.parse(data.getBytes());
        headers.parse(data2.getBytes());
        assertEquals("localhost:9001", headers.getHeaderMap().get("host"));
        assertEquals("anotherTestHeader", headers.getHeaderMap().get("test"));
    }

    @Test
    public void TestValidDone() throws IOException {
        Headers headers = new Headers();
        String data = "Host: localhost:9001\r\n";
        String data2 = "Test: anotherTestHeader\r\n";
        String data3 = "\r\n";

        headers.parse(data.getBytes());
        assertFalse(headers.isDone());
        headers.parse(data2.getBytes());
        assertFalse(headers.isDone());
        headers.parse(data3.getBytes());
        assertTrue(headers.isDone());
    }

    @Test
    public void TestValidFieldNameWithCaseInsensitivity() throws IOException {
        Headers headers = new Headers();
        String data = "HoSt: localhost:9001\r\n\r\n";
        headers.parse(data.getBytes());
        assertEquals("localhost:9001", headers.getHeaderMap().get("host"));
    }

    @Test
    public void TestValidHeaderWithMultipleValues() throws IOException {
        Headers headers = new Headers();
        String data = "Host: localhost:9001\r\n";
        String data2 = "Host: anotherhost:9002\r\n";
        String data3 = "Host: anotheranotherhost:9003\r\n\r\n";

        headers.parse(data.getBytes());
        headers.parse(data2.getBytes());
        headers.parse(data3.getBytes());
        assertEquals("localhost:9001, anotherhost:9002, anotheranotherhost:9003", headers.getHeaderMap().get("host"));
    }

    @Test
    public void TestInvalidSpacing() throws IOException {
        Headers headers = new Headers();
        String data = "Host : localhost:9001\r\n\r\n";
        assertThrows(IOException.class, () -> headers.parse(data.getBytes()));
    }

    @Test
    public void TestInvalidCharactersInFieldName() throws IOException {
        Headers headers = new Headers();
        String data = "H@st: localhost:9001\r\n\r\n";
        assertThrows(IOException.class, () -> headers.parse(data.getBytes()));
    }
}
