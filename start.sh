#!/bin/sh

nohup java -Dspring.profiles.active=qa -jar /opt/uacs/uacs-cms.jar & nohup java -Dspring.profiles.active=qa -jar /opt/uacs/uacs-web.jar