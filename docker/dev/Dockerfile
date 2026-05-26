FROM clojure:temurin-21-tools-deps-alpine

WORKDIR /app

COPY deps.edn .
RUN clojure -P -M:dev:test

CMD ["clojure", "-M:dev"]
