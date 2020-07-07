pipeline {
  agent any

  options {
    // 最多保留7天或者10条历史执行记录
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '10')
  }

  triggers {
      // 每隔五分钟运行一次
      cron '*/5 * * * * '
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