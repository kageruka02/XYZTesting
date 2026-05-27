# ============================================================
# XYZ Bank Test Automation — Docker Image
# ============================================================
# Base image: Selenium standalone Chrome (includes Chrome + ChromeDriver)
# We layer Maven + Java 21 on top so tests run fully inside the container.
#
# Build:  docker build -t xyz-testing .
# Run:    docker run --rm xyz-testing
# ============================================================

FROM selenium/standalone-chrome:latest

USER root

# ── Install Java 21 + Maven ──────────────────────────────────────────────────
RUN apt-get update && apt-get install -y --no-install-recommends \
        wget \
        curl \
        unzip \
        maven \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Temurin JDK 21
RUN mkdir -p /opt/java && \
    wget -q -O /tmp/jdk.tar.gz \
      "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_linux_hotspot_21.0.3_9.tar.gz" && \
    tar -xzf /tmp/jdk.tar.gz -C /opt/java --strip-components=1 && \
    rm /tmp/jdk.tar.gz

ENV JAVA_HOME=/opt/java
ENV PATH=$JAVA_HOME/bin:$PATH

# ── Working directory ────────────────────────────────────────────────────────
WORKDIR /app

# ── Pre-fetch Maven dependencies (layer cached until pom.xml changes) ────────
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress -q

# ── Copy project sources ─────────────────────────────────────────────────────
COPY src/       ./src/
COPY testdata/  ./testdata/
COPY testng.xml .

# ── Default command: run all tests in headless mode ──────────────────────────
CMD ["mvn", "clean", "test", \
     "-Dheadless=true", \
     "-Dbrowser=chrome", \
     "--no-transfer-progress"]
