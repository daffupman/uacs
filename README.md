# Universal Access Control System

## 使用docker启动项目注意事项

### 使用docker-compose启动服务
```shell
# 创建并启动服务（后台）
docker-compose up [-d]
# 停止服务
docker-compose stop
# 启动服务
docker-compose start
# 停止并删除服务
docker-compose down
```

### 初始化nginx
拷贝`nginx.conf`和`default.conf`到nginx容器中，替换原来的配置文件，重启nginx
```shell
docker container cp nginx.conf uacs_nginx:/etc/nginx
docker container cp default.conf uacs_nginx:/etc/nginx/conf.d
/usr/sbin/nginx -s reload
```

### 初始化mysql数据

先把doc目录下的uacs.sql放入mysql容器里，然后进入mysql容器，创建uacs并导入uacs.sql文件
```shell
docker container cp uacs.sql uacs_mysql8:/tmp/uacs.sql
docker container exec -it uacs_mysql8 sh
mysql -uroot -p

create database uacs;
use uacs;
source /tmp/uacs.sql;
```

### 初始化redis

先把docker-compose里面的6个服务中的command改成 `redis-server --requirepass root`, 然后把redis的配置文件拷贝到容器内
```shell
docker container cp doc/redis.conf uacs_redis_c1:/etc/redis/redis.conf
docker container cp doc/redis.conf uacs_redis_c2:/etc/redis/redis.conf
docker container cp doc/redis.conf uacs_redis_c3:/etc/redis/redis.conf
docker container cp doc/redis.conf uacs_redis_c4:/etc/redis/redis.conf
docker container cp doc/redis.conf uacs_redis_c5:/etc/redis/redis.conf
docker container cp doc/redis.conf uacs_redis_c6:/etc/redis/redis.conf
```
进入容器uacs_redis_c1，执行redis cluster初始化命令
```shell
docker container exec -it uacs_redis_c1 sh
redis-cli -c -a root
# 创建集群，主节点和从节点比例为1，1-3为主，4-6为从，1和4，2和5，3和6分别对应为主从关系，这也是最经典用的最多的集群模式
redis-cli -a root --cluster create 172.60.3.1:6379 172.60.3.2:6379 172.60.3.3:6379 172.60.3.4:6379 172.60.3.5:6379 172.60.3.6:6379 --cluster-replicas 1
```