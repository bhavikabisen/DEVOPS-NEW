pipeline {    
    agent {
        docker {
            image 'docker push bhavikabisen/project-jenkins:bhaviclus'
            reuseNode true
        }
    }
     parameters { // validate the string EKS_CLUSTER_NAME parameter  
        
        choice(name: 'EKS_AWS_REGION', description: 'Provide aws region', choices: ['us-east-1', 'us-east-2', 'us-west-2', 'ap-east-1', 'ap-south-1', 'ap-northeast-2', 'ap-southeast-1', 'ap-southeast-2', 'ap-northeast-1', 'ca-central-1', 'eu-central-1', 'eu-west-1', 'eu-west-2', 'eu-west-3', 'eu-north-1', 'me-south-1', 'sa-east-1'])
        string(name: "EKS_CLUSTER_NAME", defaultValue: "", description: "Provide the name of cluster")
        choice(name: 'EKS_CLUSTER_VERSION', description: 'Provide EKS version', choices: ['1.18', '1.19', '1.20', '1.21'])
        choice(name: 'EKS_NODE_COUNT', description: 'Provide the node count', choices: ['1', '2', '3'])
    }

    stages {
        stage ('cluster-create'){
            steps {
            withAWS(credentials: 'aws-creds') {
                sh (script: '''
                    eksctl create cluster --name=${EKS_CLUSTER_NAME} --region=${EKS_AWS_REGION} --version=${EKS_CLUSTER_VERSION} --nodes ${EKS_NODE_COUNT}   --managed
                    ''')    
                 }
            }
        }
    } 

}