pipeline {
    agent any
    environment {
        DB_USER = 'admin'
        DB_PASSWD = 'bhavika@456'
        PROD_IP = '3.14.254.100'
        UAT_IP = '172.31.9.213'
    }

    stages {
        stage('code pull') {
            steps {
                echo 'Hi this is bhavika'
                sh 'ls'
                echo "username of my database is ${DB_USER}"
        }
    }        
 
         stage('code build') {
            steps {
                echo 'Hi this is bhavika'
                sh 'pwd'
                echo "passward of my database is ${DB_PASSWD}"
        }
    } 
   
         stage('store artifact') {
            steps {
                echo 'Hi this is bhavika'
                sh 'whoami'
        }
    }          

         stage('deploy artifact') {
            steps {
                echo 'Hi this is bhavika' 
                sh 'hostnamectl'                                

            }
        }
    }
}