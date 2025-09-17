# Modularization with Virtualization - Introduction to Docker

This project demonstrates the deployment of a simple web application using Java, 
Maven, Docker, and AWS EC2. The application provides a minimal REST API built in Java, 
which is containerized with Docker and pushed to Docker Hub. Finally, the container is 
deployed and tested on an AWS EC2 instance, showing the complete workflow from local 
development to cloud deployment and serving as a practical exercise to understand 
containerization, image publishing, and cloud deployment of Java applications.

---

## 📦 Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/LauraRo166/AREP_Modularization-with-Virtualization-Introduction-to-Docker.git
   cd AREP_Modularization-with-Virtualization-Introduction-to-Docker
   ```

2. Make sure you have Java 17+, Maven and Docker installed:

   ```bash
   java -version
   mvn -version
   docker -v
   ```
3. Build the project:

   ```bash
   mvn clean install
   ```

## ▶️ How to Run

1. Run the following command to run the project:

   ```bash
    java -cp "target/classes;target/dependency/*" co.edu.escuelaing.dockeraws.DockerAWS
    ```

2. The server will listen on port 5000, you can open your browser and try the index file with:
   ```
   http://localhost:5000/
   ```

{Imagen puerto 500}

---

## 🏗️ Architecture

{Imagen Arquitectura}

The system is deployed on an Amazon EC2 virtual machine and encapsulated within a Docker container, ensuring portability and ease of deployment.

**1. Client:**
The end user interacts with the application through a web browser, sending HTTP requests to the server.

**2. Browser**
The client’s browser manages the static resources it receives from the server, such as HTML, JavaScript, and JPG files. These resources form the interface with which the user interacts.

**3. Backend (Docker container on EC2)**
On the server side, the web application runs inside the Docker container.

 - The main component is the HttpServer, which handles all incoming HTTP requests.

The HttpServer can:
    
- Serve static files (HTML, JS, images) through the StaticFiles module.
- Delegate dynamic requests to the RestController.

The RestController processes business logic requests and uses Annotations to route methods and properly handle responses.

---
## 🧩 Class Diagram

{Imagen diagramas de clases}

---
## 🔌Concurrency and Graceful Shutdown 
The application implements a graceful shutdown mechanism to ensure that the server does not 
terminate abruptly when the container or process is stopped. This mechanism is handled through
a shutdown hook registered in the Java runtime. When the application receives a termination 
signal (e.g., SIGTERM when executing docker stop), the shutdown hook is triggered.

At that moment, the server stops accepting new incoming connections and properly terminates 
ongoing requests. Once all resources are released and the execution flow is safely completed, 
the server shuts down.

This approach ensures that the system avoids data loss, incomplete responses, or corrupted 
states, while also making the application behave predictably inside a Docker container or any 
managed environment.

````java
    /**
     * Starts the HTTP server, listening on port 35000. This method accepts
     * incoming connections, parses requests, and dispatches them either to a
     * registered service method (for paths starting with "/app") or serves
     * static files from the "webroot" directory.
     *
     * @throws IOException if an I/O error occurs while handling client
     * connections.
     * @throws URISyntaxException if there is an error resolving file paths.
     */
    public static void runServer(int port) throws IOException, URISyntaxException {
        loadServices();
        threadPool = Executors.newFixedThreadPool(10);

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado en el puerto " + port);
        } catch (IOException e) {
            throw new IOException("No se pudo iniciar el servidor en el puerto " + port, e);
        }

        // Shutdown elegante
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor...");
            stopServer();
        }));

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClient(clientSocket));
            }
        } catch (SocketException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println("Servidor detenido.");
            } else {
                e.printStackTrace();
            }
        }
    }
    /**
     * Stops the server gracefully by closing the server socket and shutting down the thread pool.
     * The method ensures that:
     * The serverSocket is closed if it is active.
     * The threadPool is instructed to shut down, allowing ongoing tasks to finish.
     * If tasks do not complete within 5 seconds, the thread pool is forcibly terminated.
     * This guarantees that the server releases resources and stops without abruptly interrupting active processes.
     */
    private static void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            System.out.println("Servidor apagado correctamente.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al apagar servidor: " + e.getMessage());
        }
    }

    /**
     * Handles a single client connection by reading the HTTP request, determining the requested resource,
     * and sending the appropriate response.
     *
     * @param clientSocket the socket connected to the client making the request
     */
    private static void handleClient(Socket clientSocket) {
        try (
                OutputStream out = clientSocket.getOutputStream(); BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()))) {
            String inputLine;
            URI requri = null;
            boolean firstline = true;

            while ((inputLine = in.readLine()) != null) {
                if (firstline) {
                    requri = new URI(inputLine.split(" ")[1]);
                    System.out.println("Path: " + requri.getPath());
                    firstline = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            if (requri != null && requri.getPath().startsWith("/app")) {
                String outputLine = invokeService(requri);
                out.write(outputLine.getBytes());
            } else if (requri != null) {
                serveStaticFile(requri, out);
            }

            out.flush();
        } catch (Exception e) {
            System.err.println("Error manejando cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
````
---
## ✈️ Deployment

The dockerfile is created in the root directory with the following content:

```
FROM openjdk:17
 
WORKDIR /usrapp/bin
 
ENV PORT=6000
 
COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency
 
CMD ["java","-cp","./classes:./dependency/*","co.edu.escuelaing.dockeraws.DockerAWS"]
```

and the same goes for the docker-compose.yml file:

```
services:
  web:
    build:
        context: .
        dockerfile: Dockerfile
    container_name: web
    ports:
        - "8087:6000"
  db:
    image: mongo:8-noble
    container_name: db
    volumes:
        - mongodb:/data/db
        - mongodb_config:/data/configdb
    ports:
        - 27017:27017
    command: mongod
 
volumes:
  mongodb:
  mongodb_config:  

```

We build the image with the command
```bash
docker build --tag dockeraws .
```
{Construccion Imagen}

From the image created, create three instances of a docker container.
```bash
docker run -d -p 34000:6000 --name firstdockercontainer dockeraws
docker run -d -p 34001:6000 --name firstdockercontainer2 dockeraws
docker run -d -p 34002:6000 --name firstdockercontainer3 dockeraws
```

{prueba docker 3}

We can verify its correct creation with
```bash
docker ps .
```
{Imagen docker ps 3}

And we can test by accessing to
```
http://localhost:34001/
```
We run docker compose with
```bash
docker-compose up -d
```
{Imagen docker compose}

A repository is created on DockerHub.

{DockerHub}

and we execute:
```bash
docker tag dockeraws lauraro/arep20252
docker login
docker push lauraro/arep20252:latest
```

{push tag}

{tag repo}

Now we create the EC2 instance in AWS, access the machine and run:
```bash
sudo yum update -y
sudo yum install docker
sudo service docker start
sudo usermod -a -G docker ec2-user
```

Reboot to apply changes and execute:
```bash
docker run -d -p 42000:6000 --name dockeraws lauraro/arep20252
```

And now you can try accessing from a browser with the machine's DNS, 
for example:
```
http://ec2-54-198-75-226.compute-1.amazonaws.com:42000/```
```
---
## ✅ Evaluation (Tests)

### Unit tests
Unit tests were created using JUnit to validate the server’s functionality:

To run the tests:

   ```bash
   mvn test
   ```

Should look something like this:

{Imagen tests}

---
## 🎥 Videos

The video contains evidence of the correct operation of the Docker container deployment locally and on AWS.

[![Mira el video en YouTube](https://img.youtube.com/vi/ag_pscokff0/hqdefault.jpg)](https://www.youtube.com/watch?v=ag_pscokff0)

---

## 👩‍💻 Author

Laura Daniela Rodríguez
