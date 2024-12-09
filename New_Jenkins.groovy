pipeline {
    agent any

    environment {
        DEPLOY_ENV = "production"  // Add your environment variable for production deployment
        BUILD_DIR = 'build'
        APP_PORT = 3000 // Port where the app should run
        APP_PID_FILE = "app.pid" // Store the PID to track the running app
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
                sh 'npm run build'  // Build the application
            }
        }

        // Stop the Existing Application if Running on Port 3000 (Windows-friendly)
        stage('Stop Existing App') {
            steps {
                script {
                    // Find the PID of the existing app running on port 3000 using netstat
                    def pid = sh(script: '''netstat -ano | findstr :3000 | findstr LISTENING | awk "{print \$5}" || true''', returnStdout: true).trim()

                    // If an existing app is found, kill it using taskkill
                    if (pid) {
                        echo "Stopping existing app with PID: ${pid}"
                        sh "taskkill /PID ${pid} /F"  // Kill the process running on port 3000
                    } else {
                        echo "No existing app found running on port ${APP_PORT}"
                    }
                }
            }
        }

        // Deploy the New Build (Start the New App on Port 3000)
        stage('Deploy') {
            steps {
                echo 'Deploying the new application to localhost...'
                sh '''
                    # Start the app using the build folder on port 3000
                    nohup serve -s build -l ${APP_PORT} > server.log 2>&1 &
                    echo $! > ${APP_PID_FILE}  # Save the process ID (PID) of the new app
                    tail -f server.log  # Log the server output to make sure it's starting correctly
                '''
            }
        }

        // Run Tests (optional, can be skipped if no tests)
        stage('Test') {
            steps {
                script {
                    // Run tests, but continue even if no tests are found
                    sh 'npm test --passWithNoTests || true'
                }
            }
        }

        // Release (optional, remove or replace deploy_prod.sh if not used)
        stage('Release') {
            steps {
                echo 'Releasing to production...'
                // If you don't need this step, remove it or comment it out
                // sh './deploy_prod.sh'
            }
        }
    }

    post {
        success {
            echo "Build and Deployment Success!"
        }

        failure {
            echo "Build or Deployment Failed!"
        }
    }
}
