FROM gradle as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --stacktrace build

FROM openjdk:10-jre-slim
EXPOSE 8080
COPY --from=builder /home/gradle/src/web/build/libs/web.jar /app/
WORKDIR /app
CMD java -jar web.jar
