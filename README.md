# Honey-snow-yu-city
#### 介绍：

springboot+vue3+uniapp小程序奶茶点单系统，数据库使用的是MYSQL数据库,版本为8.0.31，jdk使用的是1.8版本，ORM框架选用的是mybatisplus。此外，本项目还使用了一些中间件：①Minio：使用该组件用于文件的上传，如商品的图片等。②Redis：利用Redis进行了商品数据的缓存，订单号id的生成，抢购业务中的分布式锁，以及将产生的订单封装为消息，利用Redis的Stream流实现一个消息队列，将消费的消息（即订单）结合WebSocket实时的发送给后台。



#### 文件目录解释：

【1】back-end：后端Springboot文件目录，一个单体项目，包含两个模块，backend模块为奶茶点单后台的服务端，mini-program模块为小程序的服务端。

【2】database：数据库目录，一个包含数据库的结构以及数据的sql文件。

【3】front-end：前端vue3+uniapp文件目录，其中tea-with-milk目录为奶茶点单后台，是一个vue3+TS的项目，tea-with-milk-miniProgram为奶茶点单小程序，是一个uniapp+TS的项目。



#### 数据库员工以及店铺密码：

数据库中包含一张员工表employee和一张店铺表shop,它们的密码都是经过加密处理的，因此需要告知开发者原始密码。

【1】表employee：

管理员：admin123456。除管理员外，其他员工的密码都为pwy123456。

【2】表shop

所有店铺密码均为123456。
