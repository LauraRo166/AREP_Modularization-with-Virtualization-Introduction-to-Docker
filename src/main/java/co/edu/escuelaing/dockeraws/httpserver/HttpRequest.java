package co.edu.escuelaing.dockeraws.httpserver;

import java.net.URI;

/**
 *
 * @author luisdanielbenavidesnavarro
 */
public class HttpRequest {
    
    URI requri = null;

    HttpRequest(URI requri) {
        this.requri = requri;
    }

    public String getValue(String paramName) {
        
        //Extrae el valor de paramName desde el query.
        String paramValue = requri.getQuery().split("=")[1]; //Ejemplo: /app/hello?name=jhon
        return paramValue;
    }

}
