# Java Spring Project

This project is a Java Spring application with Docker configuration using Docker Compose.

## Prerequisites

Before running this project, ensure you have the following installed:

- Docker
- Docker Compose

## Usage

### Environment Configuration

This project requires some sensitive configuration values to be stored in a `.env` file for security purposes. Follow these steps to set up the `.env` file:

Copy the provided `.env.sample` file to create a new `.env` file:

   ```bash
   cp .env.sample .env
   ```
Open the .env file in a text editor and add your Cloudinary credentials:
dotenv
Copy code
```bash
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```
Alternatively, you can use the following Make command to create and initialize the .env file interactively:

Copy code
```bash
make init-env
````
This command will prompt you to enter your Cloudinary credentials, and it will automatically populate the .env file.

Note: Ensure that the .env file is never committed to version control as it contains sensitive information. The .gitignore file is configured to ignore .env.

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
