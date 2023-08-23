# Use the official Jenkins LTS image as the base
FROM jenkins/jenkins:lts

# Switch to the root user to install dependencies
USER root

# Install necessary tools and dependencies
RUN apt-get update && \
    apt-get install -y \
    curl \
    git \
    docker.io \
    unzip

# Install eksctl
RUN curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp && \
    mv /tmp/eksctl /usr/local/bin

# Install awscli
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    sudo ./aws/install

# Switch back to the Jenkins user
USER jenkins

# Install Jenkins plugins (if needed)
# Example: RUN /usr/local/bin/install-plugins.sh <plugin1> <plugin2> ...

# Copy Jenkins pipeline script to the container
COPY ./Jenkinsfile /var/jenkins_home/Jenkinsfile

# Set permissions for the Jenkinsfile
USER root
RUN chown jenkins:jenkins /var/jenkins_home/Jenkinsfile
USER jenkins

# Start Jenkins
CMD ["/sbin/tini", "--", "/usr/local/bin/jenkins.sh"]
