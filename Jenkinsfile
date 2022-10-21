#!groovy
pipeline {
   agent any
   stages {
//     stage('Checkout') {
//       steps {
//         script {
//            // The below will clone your repo and will be checked out to master branch by default.
//            // git credentialsId: 'jenkins-user-github', url: 'https://github.com/aakashsehgal/FMU.git'
//            git url: 'https://github.com/DilipMeghwal/restassured-allure-report-sample.git'
//            // Do a ls -lart to view all the files are cloned. It will be cloned. This is just for you to be sure about it.
//            sh "ls -lart ./*"
//            // List all branches in your repo.
//            sh "git branch -a"
//            // Checkout to a specific branch in your repo.
//            sh "git checkout main"
//           }
//        }
//     }
    stage('Test on unix') {
        when {
            expression {
                isUnix()==true
            }
        }
        steps {
           sh 'mvn clean test'
           sh 'mvn allure:report'
        }
    }
    stage('Test on window') {
            when {
                expression {
                    isUnix()==false
                }
            }
            steps {
                bat 'mvn clean test'
                bat 'mvn allure:report'
            }
    }
    stage('reports') {
        steps {
            script {
                allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
  }
}