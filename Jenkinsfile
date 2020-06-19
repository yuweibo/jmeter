pipeline {
  agent any
  
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