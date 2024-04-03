FROM --platform=$BUILDPLATFORM openjdk:17-bullseye as builder
SHELL ["/bin/bash", "-c"]
WORKDIR /build
RUN apt update && apt install zip
RUN apt install apt-transport-https curl gnupg -yqq
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt update
RUN apt install sbt
COPY project/build.properties project/plugins.sbt project/
COPY build.sbt build.sbt
COPY src src
RUN sbt compile
RUN sbt pack

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /build/target/pack/ .
RUN mkdir -p /tmp/weatherflow-proxy
ENV CONFIG_DIR=/tmp/weatherflow-proxy/
ENV STATION_ID=123456
ENTRYPOINT ["bin/main"]

