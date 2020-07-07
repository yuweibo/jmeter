pipeline {
  agent any

  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '10')
  }

  triggers {
    cron 'H/1 * * * *'
  }

  stages {
    stage('tttt') {
      parallel {
        stage('tttt') {
          steps {
            sh 'ls'
            echo 'hello'
          }
        }

        stage('uuuu') {
          steps {
            sh 'ls'
          }
        }

      }
    }

    stage('iiii') {
      steps {
        sh 'ls'
      }
    }

  }
}