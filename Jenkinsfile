#!groovy
pipeline {
   agent any
    environment {
        allureTestResults = "No data present"
    }
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
stage('Running Tests') {
            steps {
                script {
                    try {
                        if (isUnix() == true) {
                            echo "Running on environment : ${env.testEnv}"
                            sh """mvn clean test -DtestEnv=${env.testEnv}"""
                        } else {
                            echo "Running on environment : ${env.testEnv}"
                            bat "mvn clean test -DtestEnv=${env.testEnv}"
                        }
                    } catch (ex) {
                        throw ex;
                    } finally {
                        if (isUnix() == true) {
                            echo "Generating allure report"
                            sh """mvn allure:report exec:java"""
                        } else {
                            echo "Generating allure report"
                            bat "mvn allure:report exec:java"
                        }
                        try {
                            def counter = 10
                            while (!(fileExists('target/temp/AllureTestResults.txt')) && counter > 0) {
                                echo "Looking for AllureTestResults.txt file"
                                sleep(time: 1, unit: "SECONDS")
                                counter--
                            }
                            allureTestResults = readFile file: 'target/temp/AllureTestResults.txt'
                        } catch (Exception e) {
                            echo "exception occurred while reading AllureTestResults : " + e
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                allure([
                        includeProperties: false,
                        jdk              : '',
                        properties       : [],
                        reportBuildPolicy: 'ALWAYS',
                        results          : [[path: 'target/allure-results']]
                ])
            }

            emailext attachLog: false, to: '$DEFAULT_RECIPIENTS',
                    subject: "ERP Services - Jenkins Build : ${currentBuild.currentResult} : ${env.JOB_NAME}",
                    body: '''${SCRIPT,template="groovy-html.template"}''' + "\n<br>" +
                            "<table class=\"section\">\n" +
                            "    <tr class=\"tr-title\">\n" +
                            "      <td class=\"td-title\" colspan=\"2\">ENVIRONMENT</td>\n" +
                            "    </tr>" +
                            "    <tr>\n" +
                            "      <td>${params.testEnv}</td>\n" +
                            "    </tr>" +
                            "</table>" +
                            "<br/>" +
                            "<table class=\"section\">\n" +
                            "    <tr class=\"tr-title\">\n" +
                            "      <td class=\"td-title\" colspan=\"2\">TEST RESULTS</td>\n" +
                            "    </tr>" +
                            "    <tr>\n" +
                            "      <td>${allureTestResults}</td>\n" +
                            "    </tr>" +
                            "</table>" +
                            "<br/>" +
                            "<p>This is auto generated email please do not reply, for any query contact ERP services team</p>",
                    mimeType: 'text/html'
        }
    }
}
