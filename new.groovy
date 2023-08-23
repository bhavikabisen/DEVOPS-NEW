pipeline {
            agent any
           
             stages {
                    stage('code-pull') {
                        steps {
                             git branch: 'main', credentialsId: 'jenkins-cred', url: 'git@github.com:bhavikabisen/devops-terra.git'
                                
                            }
                        }
                    stage('build') {
                        steps {
                            sh 'mvn clean package'
                        }                        
                    }
             }
}               