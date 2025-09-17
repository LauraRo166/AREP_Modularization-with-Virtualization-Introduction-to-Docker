package co.edu.escuelaing.dockeraws.httpserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP response that the server sends back to a client.
 * This class provides methods to construct an HTTP response.
 * It builds a valid HTTP response string that can be written directly
 * to the output stream of a socket connection.
 *
 * @author laura.rsanchez
 */
public class HttpResponse {

    private String body = "";
    private int status = 200;
    private final Map<String, String> header = new HashMap<>();

    /**
     * Constructs a new {@code HttpResponse} with default headers:
     */
    public HttpResponse() {
        header.put("Content-Type", "text/plain; charset=UTF-8");
        header.put("Connection", "close");
    }

    /**
     * Sets the HTTP status code of the response.
     *
     * @param code    the HTTP status code (e.g., 200, 404)
     * @param message the associated reason phrase (not currently used)
     */
    public void setStatus(int code, String message) {
        this.status = code;
    }

    /**
     * Sets the body of the response.
     *
     * @param body the response body as a string
     */
    public void setBody(String body) {
        this.body = body;
        header.put("Content-Length", String.valueOf(body.getBytes().length));
    }

    /**
     * Adds or updates a response header.
     *
     * @param key   the header name
     * @param value the header value
     */
    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    /**
     * Builds the raw HTTP response string.
     * @return the complete HTTP response as a string
     */
    public String buildResponse() {
        String statusMessage;

        if (status == 200) {
            statusMessage = "OK";
        } else {
            statusMessage = "ERROR";
        }

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(status)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");

        header.forEach((k, v) -> response.append(k).append(": ").append(v).append("\r\n"));
        response.append("\r\n");
        response.append(body);

        return response.toString();
    }
}