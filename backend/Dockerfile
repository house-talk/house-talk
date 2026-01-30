# ==============================
# 1️⃣ build stage
# ==============================
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Gradle 캐시 최적화
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 복사 & 빌드
COPY src ./src
RUN ./gradlew build --no-daemon -x test


# ==============================
# 2️⃣ run stage (경량 이미지)
# ==============================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# ❗ profile 지정 없음
ENTRYPOINT ["java", "-jar", "app.jar"]
