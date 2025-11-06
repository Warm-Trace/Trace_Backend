# 빌드 단계
FROM gradle:8.10-jdk17-alpine AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew clean bootJar -x test

# 런타임 단계 (Amazon Corretto 17)
FROM amazoncorretto:17-al2023-headless
WORKDIR /
COPY --from=build /workspace/build/libs/*.jar /app.jar

# 선택: JVM/DNS 튜닝
ENV JAVA_OPTS="-Dsun.net.inetaddr.ttl=60 -Dnetworkaddress.cache.ttl=60 -Dnetworkaddress.cache.negative.ttl=0 -XX:+UseG1GC -XX:MaxRAMPercentage=75"

EXPOSE 5000
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app.jar"]