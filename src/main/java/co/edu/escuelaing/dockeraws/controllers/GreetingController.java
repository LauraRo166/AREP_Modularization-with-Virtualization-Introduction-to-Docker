package co.edu.escuelaing.dockeraws.controllers;

import co.edu.escuelaing.dockeraws.annotations.GetMapping;
import co.edu.escuelaing.dockeraws.annotations.RequestParam;
import co.edu.escuelaing.dockeraws.annotations.RestController;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A REST controller that provides a simple greeting service.
 */
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /**
     * Returns a greeting message with the provided name.
     *
     * @param name the name of the person to greet.
     *             Defaults to "World" if not provided.
     * @return a greeting message in the format "Hello {name}".
     */
    @GetMapping("/greeting")
    public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
