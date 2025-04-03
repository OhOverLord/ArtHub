COMPOSE_FILE = docker-compose.yml

# Display help message with available commands
help:
	@echo "Available commands:"
	@echo "  make run        - Build and start the Docker containers in detached mode."
	@echo "  make stop       - Stop and remove the Docker containers."
	@echo "  make logs       - Display logs from the Docker containers."
	@echo "  make restart    - Restart the Docker containers (stop and then run)."
	@echo "  make init-env   - Create .env file from .env.sample and prompt for Cloudinary credentials."
	@echo "  make help       - Display this help message."

# Build and start the Docker containers in detached mode
run:
	docker-compose -f $(COMPOSE_FILE) build
	docker-compose -f $(COMPOSE_FILE) up -d

# Stop and remove the Docker containers
stop:
	docker-compose -f $(COMPOSE_FILE) down

# Display logs from the Docker containers
logs:
	docker-compose -f $(COMPOSE_FILE) logs -f

# Restart the Docker containers (stop and then run)
restart: stop run

# Create .env file from .env.sample and prompt for Cloudinary credentials
init-env:
	@cp -n .env.sample .env || echo ".env file already exists"
	@read -p "Enter CLOUDINARY_CLOUD_NAME: " cloud_name; \
	read -p "Enter CLOUDINARY_API_KEY: " api_key; \
	read -p "Enter CLOUDINARY_API_SECRET: " api_secret; \
	sed -i '' "s/^CLOUDINARY_CLOUD_NAME=.*/CLOUDINARY_CLOUD_NAME=$$cloud_name/" .env; \
	sed -i '' "s/^CLOUDINARY_API_KEY=.*/CLOUDINARY_API_KEY=$$api_key/" .env; \
	sed -i '' "s/^CLOUDINARY_API_SECRET=.*/CLOUDINARY_API_SECRET=$$api_secret/" .env; \
	echo ".env file has been updated with your credentials."
