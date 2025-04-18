FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . /app

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/SweetSupplierManager-0.0.1-SNAPSHOT.jar"]
