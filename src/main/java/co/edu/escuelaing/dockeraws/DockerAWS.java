package co.edu.escuelaing.dockeraws;

import co.edu.escuelaing.dockeraws.httpserver.HttpServer;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author Laura
 */
public class DockerAWS {

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Starting MicroSpringBoot");

        HttpServer.runServer(getPort());
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000;
    }

}
