# Java Spring Project

This project is a Java Spring application with Docker configuration using Docker Compose.

## Prerequisites

Before running this project, ensure you have the following installed:

- Docker
- Docker Compose

## Usage

### Running the Application

1. Clone this repository:

   ```bash
   git clone git@gitlab.fit.cvut.cz:bareldan/arthub_backend.git

   ```

2. Navigate to the project directory:

   ```bash
    cd arthub_backend
   ```

3. Run the following command to build the Docker images and start the containers:

   ```bash
   docker-compose up --build
   ```

4. Access the application at `http://localhost:8080`.

5. To stop the application, press `Ctrl + C` in the terminal where the `docker-compose up` command was run.

6. To remove the Docker containers, run the following command:

   ```bash
   docker-compose down
   ```

### Running the Application in the Background

1. Access the application at `http://localhost:8080`.

2. To stop the application, press `Ctrl + C` in the terminal where the `docker-compose up` command was run.

Sonarqube is already works ! :)
# ArtHub
