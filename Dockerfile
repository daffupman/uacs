FROM openjdk:11
ENV timezone=Asia/Shanghai
RUN ls -snf /usr/share/zoneinfo/$timezone /etc/localtime && echo $timezone > /etc/timezone
ADD uacs-web/target/uacs-web.jar /opt/uacs-web.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/opt/uacs-web.jar"]