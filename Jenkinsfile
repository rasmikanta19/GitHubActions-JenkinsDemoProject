// ============================================================
// Jenkins Declarative Pipeline
// ============================================================
// This Jenkinsfile defines the CI pipeline for Jenkins.
//
// Key difference from GitHub Actions:
//   - Jenkins requires a dedicated server (or container) to
//     be installed, configured, and maintained by your team.
//   - Pipelines are written in Groovy DSL and stored in the
//     repository (Pipeline as Code), but Jenkins must poll
//     or be notified (via webhook) to pick them up.
//   - Jenkins offers rich plugin support and is highly
//     customisable for complex enterprise workflows.
// ============================================================

pipeline {

    // Run on any available Jenkins agent
    agent any

    // Configurable parameters (optional — useful for manual triggers)
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build')
    }

    // Tool installations defined in "Global Tool Configuration" in Jenkins
    tools {
        maven 'Maven-3'   // Must match the name configured in Jenkins → Tools
        jdk   'JDK-11'    // Must match the name configured in Jenkins → Tools
    }

    // Environment variables available to all stages
    environment {
        APP_NAME = 'github-actions-jenkins-demo'
        VERSION  = '1.0.0'
    }

    stages {

        // ------------------------------------------------------------------
        // Stage 1 – Checkout
        // ------------------------------------------------------------------
        stage('Checkout') {
            steps {
                echo "Checking out branch: ${params.BRANCH_NAME}"
                // Jenkins checks out the repo automatically when the job is
                // linked to SCM, but the explicit step makes the stage visible.
                checkout scm
            }
        }

        // ------------------------------------------------------------------
        // Stage 2 – Compile
        // ------------------------------------------------------------------
        stage('Compile') {
            steps {
                echo 'Compiling the project...'
                sh 'mvn compile'
            }
        }

        // ------------------------------------------------------------------
        // Stage 3 – Unit Tests
        // ------------------------------------------------------------------
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
            post {
                // Publish JUnit XML test results to the Jenkins job page
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ------------------------------------------------------------------
        // Stage 4 – Package
        // ------------------------------------------------------------------
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh 'mvn package -DskipTests'
            }
            post {
                // Archive the JAR so it can be downloaded from Jenkins
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

    } // end stages

    // ------------------------------------------------------------------
    // Post-pipeline notifications
    // ------------------------------------------------------------------
    post {
        success {
            echo "✅ Build ${env.BUILD_NUMBER} succeeded for ${env.APP_NAME} v${env.VERSION}"
        }
        failure {
            echo "❌ Build ${env.BUILD_NUMBER} failed. Check the console output for details."
        }
        always {
            // Clean up workspace after each build to save disk space
            cleanWs()
        }
    }
}
