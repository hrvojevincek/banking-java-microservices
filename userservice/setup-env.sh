#!/bin/bash
# Script to help set up environment variables for the User Service

# Create .env file
echo "Creating .env file for User Service..."
cat > .env << EOL
# AWS Region
AWS_REGION=eu-central-1

# Cognito Configuration
COGNITO_USER_POOL_ID=
COGNITO_CLIENT_ID=
COGNITO_DOMAIN=
COGNITO_LOGOUT_REDIRECT=http://localhost:3000

# Database Configuration (optional)
# SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your-db-name
# SPRING_DATASOURCE_USERNAME=your-username
# SPRING_DATASOURCE_PASSWORD=your-password
EOL

echo ".env file created. Please edit it to add your Cognito credentials."
echo "The file is git-ignored for security."
echo ""
echo "The application will automatically load variables from this .env file on startup."
echo "You can simply run the application with:"
echo "./mvnw spring-boot:run"
echo ""
echo "Note: System environment variables will take precedence over .env values"
echo "if both are defined for the same variable name." 