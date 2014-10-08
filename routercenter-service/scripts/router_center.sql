drop table route_node;
drop table route_rule;
drop table route_strategy;
drop table route;

create table route(
  route_id integer primary key auto_increment,
  route_name varchar(255) not null,      	/* 服务名称 */
  route_desc varchar(255),      		/* 路由描述 */
  route_state varchar(50),      		/* 路由状态，比如正常/冻结/待审 */
  route_incharge varchar(255),    		/* 路由责任人 */
  create_time datetime,
  last_update datetime,
  unique key (route_name)
);
/* 路由节点表，一个路由可以对应多个节点 */
create table route_node(
  node_id integer primary key auto_increment,
  route_id integer not null,		/* 所属路由的ID */
  node_type varchar(255),  		/* 路由节点的类型，auto-reg/static-reg */
  node_set varchar(255),   		/* 路由节点所属的set */
  node_host varchar(50),   		/* 路由节点的机器地址 */
  node_port integer,    		/* 路由节点的端口 */
  service_url varchar(255),		/* 路由节点的服务URL，比如'10.215.129.101:19800/trc-jobmanager/TRCJobService' */
  node_state varchar(50),   		/* 路由节点状态，比如运行/停止 */
  node_weight integer,    		/* 路由节点的权重，用于基于权重的负载均衡算法 */
  create_time datetime ,
  last_update datetime ,
  foreign key (route_id) references route(route_id),
  unique key (service_url)
);
/* 路由规则表，一个路由可以有多条路由规则
   一条路由规则定义了当消费端满足某个条件时，应该被引流到哪些路由节点.
   举例：(source_prop,source_op,source_value,destination)
     =('IP','EQUAL','192.168.1.15','10.215.129.101')
     表示当消费者IP为192.168.1.15时，其服务调用应该引流到
     10.215.129.101上的提供者 */
create table route_rule(
  rule_id integer primary key auto_increment,
  route_id integer not null,		/* 所属服务的ID */
  rule_state varchar(50),    		/* 服务路由状态，比如正常/冻结 */
  rule_type  varchar(255),          /* 规则类型，可以为HOST, METHOD_ARGS */
  source_prop varchar(255),   		/* 规则关系运算变量名 */
  source_op varchar(255),     		/* 规则关系运算符，比如=, !=， between */
  source_value varchar(255),  		/* 规则关系运算值，比如'192.168.1.15' */
  destination varchar(255),   		/* 服务提供者的IP列表 */
  create_time datetime ,
  last_update datetime ,
  foreign key (route_id) references route(route_id)
);
/* 路由策略，一个路由可以有多种策略，比如负载均衡，容灾，仲裁 */
create table route_strategy(
  strategy_id integer primary key auto_increment,
  route_id integer not null,   		/* 所属路由的ID */
  strategy_state varchar(50),    	/* 策略状态，比如正常/冻结 */
  strategy_type varchar(255),   	/* 策略类型，比如负载均衡、容灾 */
  strategy_option varchar(255), 	/* 策略 */
  strategy_config text,      		/* 策略配置 */
  create_time datetime,
  last_update datetime,
  foreign key (route_id) references route(route_id)
);
/*
Table route:
route_id  |   route_name  |  service_desc  |
   1  |   teg.tdw.query |  TDW查询服务   |   

Table route_node:
node_id |       node_serviceURL        | node_state | node_weight | route_id
   1  |  192.168.19.1:19800/appname/HelloService |  ACTIVE  |    100  |   1

Table route_rule:
rule _id | rule_state  | source_prop   |  source_op |     source_value      | destination  | route_id
   1   |  ACTIVE   |   IP    |   IN     |192.168.19.2,192.168.19.3    | 192.168.19.1 |   1
   2   |  ACTIVE   |   IP    |   NOT_IN   |192.168.19.4,192.168.19.5    |    *     |   1
   3   |  ACTIVE   |   method  |   =    |      read*        | 192.168.19.1 |   1

Table route_strategy:
strategy_id | strategy_type | strategy_option | strategy_config | route_id
   1    |   LB    | RoundRobin    |   null      |   1
   2    |   FT    | Failover    |retries=2    |   1
*/