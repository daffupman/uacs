FROM openjdk:11
ENV timezone=Asia/Shanghai
RUN apt-get update  \
    && apt-get install -y vim iputils-ping \
    && ls -snf /usr/share/zoneinfo/$timezone /etc/localtime \
    && echo $timezone > /etc/timezone
ADD uacs-web/target/uacs-web.jar /opt/uacs-web.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=qa", "-jar", "/opt/uacs-web.jar"]