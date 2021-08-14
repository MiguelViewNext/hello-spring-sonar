#!/usr/bin/env groovy

pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Test') {
            when { expression {false} }
                steps {
                    echo '\033[42m\033[97mTesting...\033[0m'
                    withGradle {
                        sh './gradlew clean test pitest'
                    }
                }
                post {
                    always {
                        junit skipPublishingChecks: true, testResults: 'build/test-results/test/TEST-*.xml'
                        jacoco execPattern: 'build/jacoco/*.exec'
                        recordIssues (enabledForFailure: true, tool: pit(pattern: 'build/reports/pitest/**/*.xml'))
                    }
                }
        }

        stage('Analisis') {
            //failFast true
            parallel {
                stage('SonarQube Analysis') {
                    when { expression {false} }
                        steps {
                            withSonarQubeEnv('sonarqube') {
                                sh './gradlew sonarqube'
                            }
                        }
                }

                stage('QA') {
                    steps {
                        withGradle {
                            sh './gradlew check'
                        }
                    }
                    post {
                        always {
                            recordIssues (
                                tools: [
                                    pmdParser (pattern: 'build/reports/pmd/*.xml'),
                                    spotBugs (pattern: 'build/reports/spotbugs/*.xml', useRankAsPriority: true)
                                ]
                            )
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo '\033[42m\033[97mBuilding...\033[0m'
		        sh 'docker-compose build'
            }
        }

        stage('Security') {
            steps {
                echo '\033[42m\033[97mSecurity analysis...\033[0m'
                sh 'trivy image --format=json --output=trivy-image.json hello-spring-testing:latest'
            }
            post {
                always {
                    recordIssues (
                            enabledForFailure: true,
                            aggregatingResults: true,
                            tool: trivy(pattern: 'trivy-*.json')
                    )
                }
            }
        }

        stage ('Publish') {
            steps {
                echo '\033[42m\033[97mPublising...\033[0m'
                withDockerRegistry([url: 'http://10.250.9.3:5050', credentialsId: 'Registry_Gitlab']) {
                    sh 'docker push 10.250.9.3:5050/movbit/hello-spring-sonar/hello-spring:latest'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '\033[42m\033[97mDeploying....\033[0m'
                sshagent (credentials: ['appKey']) {
                    sh "ssh -o StrictHostKeyChecking=no app@10.250.9.3 'cd hello-spring && docker-compose pull && docker-compose up -d'"
                }
            }
        }
        stage('gitlab') {
            steps {
                echo '\033[42m\033[97mNotify GitLab\033[0m'
                updateGitlabCommitStatus name: 'build', state: 'pending'
                updateGitlabCommitStatus name: 'build', state: 'success'
            }
        }
    }
}
