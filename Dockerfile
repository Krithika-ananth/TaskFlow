FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM tomcat:10.1-jdk17-temurin
COPY --from=build /build/target/DayFlow.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-8080}\\\"/\" \"$CATALINA_HOME/conf/server.xml\" && exec catalina.sh run"]
