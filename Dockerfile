# Use Java 21 lightweight image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy all source files
COPY . .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["sh", "-c", "java -jar target/*.jar"]
