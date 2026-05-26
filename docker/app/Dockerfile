FROM clojure:temurin-21-tools-deps-alpine AS builder

WORKDIR /app
COPY deps.edn build.clj .
RUN clojure -P -M:build

COPY . .
RUN clojure -T:build uber

# ---

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/app.jar .

EXPOSE 3000
CMD ["java", "-jar", "app.jar"]
