pipeline {
    agent any

    environment {
        // Define any environment variables if needed
    }

    stages {
        stage('Declarative: Checkout SCM') {
            steps {
                script {
                    checkout scm
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    // This checks out the Git repository again, making sure we have the latest changes
                    checkout scm
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                script {
                    // Install necessary dependencies using npm
                    sh 'npm install'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Run the build process
                    sh 'npm run build'
                }
            }
        }

        stage('Stop Existing App') {
            steps {
                script {
                    // Find the PID of the process listening on port 3000
                    def pid = sh(script: "netstat -ano | findstr :3000 | awk '{print \$5}'", returnStdout: true).trim()

                    // If a PID is found, kill the process
                    if (pid) {
                        echo "Stopping existing app with PID: $pid"
                        sh "taskkill /PID $pid /F"
                    } else {
                        echo "No application found running on port 3000."
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Your deployment logic goes here
                    // For example, if you are using a static server, you can use:
                    // sh 'serve -s build'
                    echo "Deploying application..."
                    // Add your deployment commands here
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    // Your test execution logic goes here
                    // For example, if you have tests, you can run:
                    // sh 'npm test'
                    echo "Running tests..."
                    // Add your test commands here
                }
            }
        }

        stage('Release') {
            steps {
                script {
                    // Your release logic goes here
                    // For example, tagging the release, pushing to a remote, etc.
                    echo "Releasing the application..."
                    // Add your release commands here
                }
            }
        }
    }

    post {
        always {
            // This block runs after the pipeline, regardless of success or failure
            echo "Pipeline completed"
        }
        success {
            echo "Build and deployment were successful!"
        }
        failure {
            echo "Build or deployment failed."
        }
    }
}
