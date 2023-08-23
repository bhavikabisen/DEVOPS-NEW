pipeline {
            agent any
            environment {
                        AWS_ACCESS_KEY_ID     = credentials('jenkins-aws-secret-key-id')
                        AWS_SECRET_ACCESS_KEY = credentials('jenkins-aws-secret-access-key')
                        }
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
                        }                        
                    }
                    stage('bucket-create') {
                        steps {
                            withAWS(credentials: 'jenkins-aws') {
                            sh 'aws s3 ls'
                            sh 'aws s3api create-bucket --bucket mybucket --region us-east-2'       
                            }
                        }                        
                    }
                    stage('pushed-s3') {
                        steps {
                            withAWS(credentials: 'jenkins-aws') {
                            sh 'mv /var/lib/jenkins/workspace/${JOB_NAME}/target/*.war /tmp/student-${BUILD_ID}.war'
                            sh 'aws s3 cp /tmp/student-${BUILD_ID}.war s3://mybucket'   
                            // sh 'aws s3api put-object-acl --bucket mybucket --key student-${BUILD_ID}.war --acl public-read'    
                            }
                        }                        
                    }
             }
}          