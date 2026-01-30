# 1️⃣ Java 21 실행 환경 (중요!)
FROM eclipse-temurin:21-jdk-jammy

# 2️⃣ 컨테이너 작업 디렉토리
WORKDIR /app

# 3️⃣ Gradle 관련 파일 먼저 복사 (캐시 최적화)
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle

# 4️⃣ Gradle 실행 권한 (리눅스 필수)
RUN chmod +x gradlew

# 5️⃣ 의존성 미리 다운로드
RUN ./gradlew dependencies --no-daemon

# 6️⃣ 실제 소스 코드 복사
COPY src /app/src

# 7️⃣ 애플리케이션 빌드
RUN ./gradlew build --no-daemon -x test


# 8️⃣ Spring Boot 포트
EXPOSE 8080

# 9️⃣ 실행 (⚠️ jar 이름 맞춰야 함)
CMD ["java", "-jar", "build/libs/housetalk-0.0.1-SNAPSHOT.jar"]
