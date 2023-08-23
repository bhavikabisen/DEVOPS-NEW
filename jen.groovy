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
                    stage('cli-install') {
                        steps {
                            sh 'sudo apt-get update'
                            sh 'sudo apt-get install awscli -y'
                            sh 'sudo aws --version'
                        }                        
                    }
                    stage('upload-s3') {
                        steps {
                            withAWS(credentials: 'jenkins-aws') {
                            sh 'aws s3 ls'
                            sh 'aws s3api create-bucket --bucket jenkins-bucket --create-bucket-configuration LocationConstraint=us-east-2'
                            sh 'mv /var/lib/jenkins/workspace/${JOB_NAME}/target/*.war /tmp/student-${BUILD_ID}.war'
                            sh 'aws s3 cp /tmp/student-${BUILD_ID}.war s3://jenkins-bucket'      
                            }
                        }                        
                    }
             }
}          
# if u are creating buckets outside of the us-east-1 region then u have to write this line in the code
create-bucket-configuration LocationConstraint=us-east-2'