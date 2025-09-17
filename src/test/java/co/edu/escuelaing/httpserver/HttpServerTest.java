package co.edu.escuelaing.httpserver;

import co.edu.escuelaing.dockeraws.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    @BeforeEach
    public void setup() throws Exception {
        // Cargar servicios antes de cada prueba
        var loadMethod = HttpServer.class.getDeclaredMethod("loadServices");
        loadMethod.setAccessible(true);
        loadMethod.invoke(null);
    }

    @Test
    public void testNotFoundReturnsDefaultMessage() {
        String response = HttpServer.notFound();
        assertTrue(response.startsWith("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("404 - Not Found"));
    }

    @Test
    public void testNotFoundReturnsCustomHtmlIfExists() throws Exception {
        File file = new File("target/classes/webroot/404.html");
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), "<html><body>Error 404</body></html>");

        String response = HttpServer.notFound();
        assertTrue(response.contains("<html>"));
        assertTrue(response.contains("Content-Type: text/html"));

        file.delete();
    }

    @Test
    public void testInvokeGreetingServiceWithName() {
        String response = callInvokeService(URI.create("/app/greeting?name=Laura"));
        assertTrue(response.contains("Hola Laura"));
    }

    @Test
    public void testInvokeGreetingServiceWithDefault() {
        String response = callInvokeService(URI.create("/app/greeting"));
        assertTrue(response.contains("Hola World"));
    }

    @Test
    public void testInvokeParityEvenNumber() {
        String response = callInvokeService(URI.create("/app/parity?number=4"));
        assertTrue(response.contains("es par"));
    }

    @Test
    public void testInvokeParityOddNumber() {
        String response = callInvokeService(URI.create("/app/parity?number=5"));
        assertTrue(response.contains("es impar"));
    }

    @Test
    public void testInvokeParityInvalidNumber() {
        String response = callInvokeService(URI.create("/app/parity?number=abc"));
        assertTrue(response.contains("Por favor ingresa un número válido"));
    }

    @Test
    public void testNotFoundHasContentLength() {
        String response = HttpServer.notFound();
        assertTrue(response.contains("Content-Length"), "Debe contener Content-Length en headers");
    }

    /**
     * Helper to call private invokeService method via reflection.
     */
    private String callInvokeService(URI uri) {
        try {
            var method = HttpServer.class.getDeclaredMethod("invokeService", URI.class);
            method.setAccessible(true);
            return (String) method.invoke(null, uri);
        } catch (Exception e) {
            fail("Error invoking private method invokeService: " + e.getMessage());
            return null;
        }
    }
}