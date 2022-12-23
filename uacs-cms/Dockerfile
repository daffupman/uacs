FROM openjdk:11
ENV timezone=Asia/Shanghai
RUN apt-get update  \
    && apt-get install -y vim iputils-ping inetutils-telnet iproute2 \
    && ls -snf /usr/share/zoneinfo/$timezone /etc/localtime \
    && echo $timezone > /etc/timezone
ADD target/uacs-cms.jar /opt/uacs-cms.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=qa", "-jar", "/opt/uacs-cms.jar"]