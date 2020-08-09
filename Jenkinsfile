#!groovy
@Library('ci-scripts') _

pipeline {
    agent {
        docker { image 'maven:3.5.3-jdk-10-slim' }
    }

    stages {
        stage('Test') {
            environment {
                CODECOV_TOKEN = '5b1293bb-5536-4f60-bfa3-93b4d15eefeb'
            }
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
                    sh 'curl -s https://codecov.io/bash | bash'
                }
            }
        }

        stage('Deploy') {
            steps {
                configFileProvider([configFile(fileId: 'e634cf5d-12c6-41fa-82f9-8bbe8c594220', variable: 'MAVEN_GLOBAL_SETTINGS')]) {
                    sh "mvn --batch-mode release:update-versions -DdevelopmentVersion=${env.BRANCH_NAME}-SNAPSHOT"
                    sh "mvn -gs $MAVEN_GLOBAL_SETTINGS clean deploy -DskipTests -DaltDeploymentRepository=nexus-repository::default::${env.NEXUS_REPO}"
                }
            }
        }
    }

    post {
        always {
            pipelineUtils('cleanWorkSpace')
        }
    }
}
