pipeline {
  agent any
  stages {
    stage('tttt') {
      parallel {
        stage('s1') {
          agent {
            docker {
              image 'ubuntu:18.04'
            }

          }
          steps {
            sh 'ls'
            echo 'hello'
            ws(dir: 's1') {
              sh '''
pwd'''
            }

          }
        }

        stage('s2') {
          steps {
            sh 'ls'
          }
        }

      }
    }

  }
  options {
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '10'))
  }
  triggers {
    cron('*/5 * * * * ')
  }
}