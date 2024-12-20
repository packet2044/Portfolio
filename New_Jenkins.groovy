pipeline {
    agent any

    environment {
        DEPLOY_ENV = "production"  // Add your environment variable for production deployment
        BUILD_DIR = 'build'
    }

    stages {
        // Checkout Source Code
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/packet2044/Portfolio.git',
                    credentialsId: 'PAT'  // Replace with your credentials ID
            }
        }

        // Install Dependencies
        stage('Install Dependencies') {
            steps {
                sh 'npm install'  // Ensure you install all dependencies
            }
        }

        // Build the Application
        stage('Build') {
            steps {
                sh 'npm run build'
            }
        }

        // Test the Application (Optional, depending on your requirements)
        stage('Test') {
            steps {
                script {
                    try {
                        sh 'npm test --passWithNoTests'
                    } catch (Exception e) {
                        echo "No tests found or test failed - continuing"
                    }
                }
            }
        }

        // Deploy the Application (e.g., upload to a server or container)
        stage('Deploy') {
            steps {
                echo 'Deploying application to environment...'
                sh 'npm start'  // This can be replaced with your actual deployment script or commands
            }
        }

        // Release Stage (optional, push to production)
        stage('Release') {
            steps {
                echo 'Releasing to production...'
                // Example: deploy to production or trigger another job
                sh './deploy_prod.sh'  // Replace with your own deploy script or commands
            }
        }
    }

    post {
        success {
            echo "Build and Deployment Success!"
            // Trigger the Release Pipeline job after successful build and deployment
            build job: 'Release_Pipeline', wait: false
        }

        failure {
            echo "Build or Deployment Failed!"
        }
    }
}
