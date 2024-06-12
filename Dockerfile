FROM openjdk:17

WORKDIR /app

# Copy your project source code
COPY . .

# Install Maven dependencies
RUN ./mvnw clean package

# Run the program (assuming compiled class is in target/classes)
CMD ["java", "-cp", "matsim-example-project-0.0.1-SNAPSHOT.jar", "org.matsim.project.RunMatsim"]