package co.edu.escuelaing.dockeraws.controllers;

import co.edu.escuelaing.dockeraws.annotations.GetMapping;
import co.edu.escuelaing.dockeraws.annotations.RequestParam;
import co.edu.escuelaing.dockeraws.annotations.RestController;

/**
 * A REST controller that provides a service to check whether
 * a given number is even or odd.
 */
@RestController
public class ParityController {
    /**
     * Checks if the provided number is even or odd.
     *
     * @param number the input number as a string. If missing or invalid,
     *               the default value is "0".
     * @return a message indicating whether the number is even, odd,
     *         or an error message if the input is not a valid integer.
     */
    @GetMapping("/parity")
    public static String checkParity(@RequestParam(value = "number", defaultValue = "0") String number) {
        try {
            int n = Integer.parseInt(number);
            return (n % 2 == 0) ? (n + " es par") : (n + " es impar");
        } catch (NumberFormatException e) {
            return "Por favor ingresa un número válido.";
        }
    }
}