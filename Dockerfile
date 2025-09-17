
# Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
# Click nbfs://nbhost/SystemFileSystem/Templates/Other/Dockerfile to edit this template

FROM openjdk:17
 
WORKDIR /usrapp/bin
 
ENV PORT=6000
 
COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency
 
CMD ["java","-cp","./classes:./dependency/*","co.edu.escuelaing.dockeraws.DockerAWS"]