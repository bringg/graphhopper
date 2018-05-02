#!groovy

pipeline {
    agent {
        dockerfile { filename 'Dockerfile.ci' }
    }

    stages {
        stage('Test') {
            environment {
                CODECOV_TOKEN = '5ecccbfe-730c-4a46-801d-f6e539cd97e9'
            }
            steps {
                sh 'mvn test'
                sh 'curl -s https://codecov.io/bash | bash'
            }
            post {
                always {
                    junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
                }
            }
        }
    }
}
