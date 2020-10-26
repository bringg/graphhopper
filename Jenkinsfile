#!groovy
@Library('ci-scripts') _

pipeline {
    agent any

    stages {
        stage('Test') {
            environment {
                CODECOV_TOKEN = '5ecccbfe-730c-4a46-801d-f6e539cd97e9'
            }
            steps {
                script {
                    docker.image('maven:3.5.3-jdk-10-slim').inside() {
                        sh 'mvn test'
                    }
                }
            }
            post {
                always {
                    junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
                    sh 'curl -s https://codecov.io/bash | bash'
                }
            }
        }

        stage('Push to Maven') {
            when {
                expression { env.BRANCH_NAME in ['master', 'staging'] }
            }

            steps {
                deployToMaven()
            }
        }
    }

    post {
        always {
            pipelineUtils('cleanWorkSpace')
        }
    }
}
