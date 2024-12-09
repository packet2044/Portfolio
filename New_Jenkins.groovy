pipeline {
    agent any
    
    environment {
        // Set the necessary environment variables (if any)
        GIT_REPO_URL = 'https://github.com/packet2044/Portfolio.git'
        DEPLOY_DIR = '/var/www/html'
        // Add more variables if necessary
    }

    stages {
        // Stage 1: Checkout code from the repository
        stage('Checkout Code') {
            steps {
                echo 'Cloning repository from GitHub...'
                git url: "${GIT_REPO_URL}", branch: 'main'
            }
        }

        // Stage 2: Build the application (this can be adjusted for your build tool, e.g., npm, Maven, etc.)
        stage('Build Application') {
            steps {
                echo 'Building the application...'
                // Replace this with your build command (e.g., npm install, mvn package, etc.)
                sh 'npm install'  // Example for Node.js app
            }
        }

        // Stage 3: Deploy the application (this will overwrite the existing app)
        stage('Deploy Application') {
            steps {
                echo 'Deploying application to the server...'
                // Replace this with your deployment command, e.g., Docker, PM2, etc.
                sh '''
                rm -rf ${DEPLOY_DIR}/*
                cp -r * ${DEPLOY_DIR}/
                # Add deployment commands if necessary, for example:
                # pm2 restart app  # Example for Node.js app
                '''
            }
        }

        // Stage 4: Verify that the app is running
        stage('Post-Deployment Check') {
            steps {
                echo 'Verifying that the application is running...'
                // Add your health check or verification command here
                // For example, ping the app's health endpoint or check the status with PM2, Docker, etc.
                sh 'curl -f http://localhost'  // Example health check for a web app
            }
        }
    }

    post {
        success {
            echo 'Application deployed successfully and is live!'
        }
        failure {
            echo 'There was an error deploying the application.'
        }
    }
}
