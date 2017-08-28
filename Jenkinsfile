#!/usr/bin/env groovy

pipeline {
  agent { label 'executor-v2' }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  stages {
    stage('Create and archive the Maven package') {
      steps {
        echo 'TODO'
      }
    }
    stage('Run tests and archive test results') {
      steps {
        lock("api-java-${env.NODE_NAME}") {
          sh './test.sh'
        }

        junit 'target/surefire-reports/*.xml'
      }
    }

    stage('Publish the Maven package') {
      when {
        branch 'master'
      }
      steps {
        echo 'TODO'
      }
    }
  }

  post {
    always {
      sh 'docker run -i --rm -v $PWD:/src -w /src alpine/git clean -fxd'
      deleteDir()
    }
    failure {
      slackSend(color: 'danger', message: "${env.JOB_NAME} #${env.BUILD_NUMBER} FAILURE (<${env.BUILD_URL}|Open>)")
    }
    unstable {
      slackSend(color: 'warning', message: "${env.JOB_NAME} #${env.BUILD_NUMBER} UNSTABLE (<${env.BUILD_URL}|Open>)")
    }
  }
}
