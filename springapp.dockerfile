FROM openjdk:8


RUN  \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update -y && \
  apt-get install -y maven

# create working directory
RUN mkdir -p /vol/development
COPY ./friends-management-0.0.1-SNAPSHOT.jar /vol/development
WORKDIR /vol/development

# maven exec
# RUN mvn clean install -T4 -Dmaven.test.skip=true
ENTRYPOINT exec java -jar /vol/development/friends-management-0.0.1-SNAPSHOT.jar