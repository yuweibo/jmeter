pipeline {
    agent any

    stages {


        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }

        stage("p build"){
            parallel {
               stage("build") {

            stages('builds'){
                stage('maven build'){
                    steps {
                        sh 'touch a.txt'
                        sh 'pwd'
                        sh 'ls'
                        sh 'mvn -v'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            image 'maven:3.6.3-jdk-8-openj9'
                        }
                    }
                }
                stage('node build'){
                    steps {
                        sh 'pwd'
                        sh 'ls'
                        sh 'node -v'
                        sh 'npm -v'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            image 'node:14.5.0'
                        }
                    }
                }

                stage('docker build'){
                    steps {
                        sh 'pwd'
                        sh 'ls'
                        sh 'which docker'
                        sh 'docker -v'
                        sh 'docker ps'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            args '-v  "/var/run/docker.sock:/var/run/docker.sock" '
                            image 'docker:19.03.12'
                        }
                    }
                }
            }
        }
                stage("build1") {

            stages('builds'){
                stage('maven build'){
                    steps {
                        sh 'touch a.txt'
                        sh 'pwd'
                        sh 'ls'
                        sh 'mvn -v'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            image 'maven:3.6.3-jdk-8-openj9'
                        }
                    }
                }
                stage('node build'){
                    steps {
                        sh 'pwd'
                        sh 'ls'
                        sh 'node -v'
                        sh 'npm -v'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            image 'node:14.5.0'
                        }
                    }
                }

                stage('docker build'){
                    steps {
                        sh 'pwd'
                        sh 'ls'
                        sh 'which docker'
                        sh 'docker -v'
                        sh 'docker ps'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            args '-v  "/var/run/docker.sock:/var/run/docker.sock" '
                            image 'docker:19.03.12'
                        }
                    }
                }
            }
        }
            }
        }

        stage("build1") {

            stages('builds'){
                stage('maven build'){
                    steps {
                        sh 'touch a.txt'
                        sh 'pwd'
                        sh 'ls'
                        sh 'mvn -v'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            image 'maven:3.6.3-jdk-8-openj9'
                        }
                    }
                }
                stage('node build'){
                    steps {
                        sh 'pwd'
                        sh 'ls'
                        sh 'node -v'
                        sh 'npm -v'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            image 'node:14.5.0'
                        }
                    }
                }

                stage('docker build'){
                    steps {
                        sh 'pwd'
                        sh 'ls'
                        sh 'which docker'
                        sh 'docker -v'
                        sh 'docker ps'
                    }
                    agent {
                        docker {
                            customWorkspace "${WORKSPACE}/s1"
                            args '-v  "/var/run/docker.sock:/var/run/docker.sock" '
                            image 'docker:19.03.12'
                        }
                    }
                }
            }
        }

    }
}


