# first do a quick gradlew build, then run:
# podman build -t mqlvin/ctrlclubserver . 
# to build the image w this file

FROM docker.io/eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/server-all.jar server.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "server.jar"]

# then after:
# podman save localhost/mqlvin/ctrlclubserver:latest -o ctrlclubserver.tar  
# and then just scp it into the server
