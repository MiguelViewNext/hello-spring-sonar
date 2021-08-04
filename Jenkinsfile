#!/usr/bin/env groovy

pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Test') {
            steps {
                 echo 'Testing..'
                 withGradle {
                    sh './gradlew clean test'
                 }

                 //sh './gradlew test'
                 //archiveArtifacts artifacts: 'build/test-results/test/binary/*.xml'

                 //junit skipPublishingChecks: true, testResults: 'build/test-results/test/TEST-*.xml'

            }
            post {
               always {
                     junit skipPublishingChecks: true, testResults: 'build/test-results/test/TEST-*.xml'
                     jacoco execPattern: 'build/jacoco/*.exec'
                     //sh './gradlew check pmdMain'
               }
            }
        }

        /*stage('SonarQube Analysis') {
            when {

            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh './gradlew sonarqube'
                }
            }
        }*/

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
                            pitmutation killRatioMustImprove: false, minimumKillRatio: 50.0, mutationStatsFile: 'build/reports/pitest/**/mutations.xml'
                        ]
                    )
                }
            }
        }

        stage('Build') {
            steps {
		        //sh './gradlew assemble'
		        sh 'docker-compose build'
            }
            //post {
        		//success {
		        	//archiveArtifacts artifacts: 'build/libs/*.jar'
                    //sh 'trivy image hello-spring-testing:latest'
		      //  }
	        //}
        }

        stage('Security') {
            steps {
                echo 'Security analysis...'
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

        stage('Deploy') {
            steps {
                echo 'Deploying....'
                //sh 'docker-compose up -d'
                //sh 'java -jar build/libs/hello-spring-0.0.1-SNAPSHOT.jar'
            }
        }
        //stage('gitlab') {
        //    steps {
        //         echo 'Notify GitLab'
        //         updateGitlabCommitStatus name: 'build', state: 'pending'
        //         updateGitlabCommitStatus name: 'build', state: 'success'
        //    }
        //}
    }
}
