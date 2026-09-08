# syntax=docker/dockerfile:1
#
# Bygger en avbild av webbapplikationen.
#
# Evangelieboken med bibeltext ingår medvetet INTE. Den är upphovsrättsskyddad
# och ligger i .dockerignore, så den kan inte följa med ens av misstag.
# Montera in den vid körning och peka ut den:
#
#   podman run --rm -p 8080:8080 \
#     -v /srv/lektionarium/svk_lektionarium.xml:/data/lektionarium.xml:ro \
#     -e LEKTIONARIUM_LECTIONARYFILE=/data/lektionarium.xml \
#     lektionarium
#
# Utan filen startar den ändå, med enbart bibelhänvisningar.

# ---------- Bygg ----------
FROM docker.io/library/eclipse-temurin:21-jdk AS build

# Maven Wrapper väljer .tar.gz i stället för .zip när unzip saknas, och då
# stämmer inte den pinnade sha256-summan. Felmeddelandet påstår att
# distributionen kan vara komprometterad, men orsaken är bara att ett annat
# arkivformat hämtades.
RUN apt-get update \
 && apt-get install --yes --no-install-recommends unzip \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /build

# Pom-filerna först. Beroendena hamnar då i ett eget lager som bara byggs om
# när en pom ändras, inte vid varje kodändring.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY api/pom.xml api/
COPY cli/pom.xml cli/
COPY web/pom.xml web/
RUN ./mvnw -B --no-transfer-progress -pl web -am dependency:go-offline -DskipTests

COPY api/ api/
COPY web/ web/
RUN ./mvnw -B --no-transfer-progress -pl web -am package -DskipTests

# Dela upp jar-filen i lager. Beroenden ändras sällan och kan då återanvändas
# mellan avbilder, medan applikationslagret är litet och byggs om ofta.
RUN java -Djarmode=tools -jar web/target/lektionarium-web-*.jar \
      extract --layers --launcher --destination /app

# ---------- Kör ----------
FROM docker.io/library/eclipse-temurin:21-jre

RUN groupadd --system --gid 10001 lektionarium \
 && useradd --system --uid 10001 --gid 10001 --home-dir /app --no-create-home lektionarium

WORKDIR /app

# Ordningen är den lagren ändras i: minst föränderligt först.
COPY --from=build --chown=10001:10001 /app/dependencies/ ./
COPY --from=build --chown=10001:10001 /app/spring-boot-loader/ ./
COPY --from=build --chown=10001:10001 /app/snapshot-dependencies/ ./
COPY --from=build --chown=10001:10001 /app/application/ ./

USER 10001:10001
EXPOSE 8080

# JVM:en läser JAVA_TOOL_OPTIONS av sig själv, så starten kan ske utan skal.
# MaxRAMPercentage får den att rätta sig efter containerns minnesgräns i
# stället för efter värdens totala minne.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
