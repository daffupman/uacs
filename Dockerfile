FROM openjdk:11
ENV timezone=Asia/Shanghai
RUN apt-get update  \
    && apt-get install -y vim iputils-ping inetutils-telnet iproute2 \
    && ls -snf /usr/share/zoneinfo/$timezone /etc/localtime \
    && echo $timezone > /etc/timezone
ADD start.sh /opt/uacs/start.sh
ADD uacs-cms/target/uacs-cms.jar /opt/uacs/uacs-cms.jar
ADD uacs-web/target/uacs-web.jar /opt/uacs/uacs-web.jar
ENTRYPOINT ["sh", "/opt/uacs/start.sh"]