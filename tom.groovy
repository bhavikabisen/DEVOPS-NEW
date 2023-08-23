pipeline {
    agent any
    stages {
        stage('code-pulling') {
            steps {
                git branch: 'main', credentialsId: 'jenkins-cred', url: 'git@github.com:bhavikabisen/devops-terra.git'
            }
        }
        stage("build-maven"){
            steps{
                sh 'sudo apt-get update'
                sh 'sudo apt install maven'
                sh 'sudo mvn clean package' 
            }    
        }
        stage('artifact-to-s3') {
            steps {
                withAWS(credentials: 'jenkins-aws', region: 'us-east-2') {
                     sh'''
                     sudo apt update -y
                     sudo apt install awscli -y
                     aws s3 ls
                     aws s3 mb s3://56lj-bucket --region us-east-2
                     sudo mv /var/lib/jenkins/workspace/${JOB_NAME}/target/*.war /tmp/student-${BUILD_ID}.war
                     aws s3 cp /tmp/student-${BUILD_ID}.war s3://56lj-bucket
                    '''
                }
            }     
        }
        stage('deploy-to-tomcat-server') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-cred', keyFileVariable: 'id_rsa', usernameVariable: 'tomcat')]) {
                    sh'''
                    sudo sudo ssh -i ${id_rsa.pub} -T -o user@3.14.254.100 <<EOF
                    sudo apt update -y
                    sudo apt install awscli -y
                    sudo apt install openjdk-11-jre -y
                    sudo mkdir /var/jenkins
                    sudo chown ubuntu:ubuntu /var/jenkins
                    export AWS_ACCESS_KEY_ID=AKIA36KQLZV76X7HSHOF
                    export AWS_SECRET_ACCESS_KEY=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
                    export AWS_DEFAULT_REGION=us-east-2
                    aws s3 ls
                    aws s3 cp /tmp/student-${BUILD_ID}.war s3://56lj-bucket
                    curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.74/bin/apache-tomcat-9.0.74.tar.gz
                    sudo tar -xzvf apache-tomcat-9.0.74.tar.gz -C /opt/                 
                    sudo cp -rv /tmp/student-${BUILD_ID}.war
                    sudo cp -rv student.war /opt/apache-tomcat-9.0.74/webapps/
                    sudo sh /opt/apache-tomcat-9.0.74/bin/startup.sh
                    '''
                }
            }
        }
    }
}
    