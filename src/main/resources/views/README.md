#  Mongodb-WeAdmin

#### 项目介绍
- Mongodb网页管理工具,基于Spring Boot2.0，前端采用layerUI实现。
- 源于线上环境部署mongodb时屏蔽了外网访问mongodb,所以使用不了mongochef这样方便的远程连接工具，所以Mongodb提供的java api实现的的网页版实现
- 未设置登录权限相关模块，低耦合性 方便复制代码嵌入到现有的项目中


#### 软件架构
springBoot2.0
mongodb
layerUI

### 功能说明
- Mongodb的多数据库查询
- Mongodb的多数据对应下的表查询
- Mongodb的指定表下的数据列表查询
- Mongodb集合的增删改查
- MongoDB 条件操作符查询 ` $gt, $lt, $gte,  $lte, $ne, $in, $regex  等等 `
- MongoDB 聚合 aggregate() 方法（开发中）

### 启动运行
1. application.properties 配置mongodb服务地址
1. MongoWebSelectApplication 运行启动
1. 访问 http://localhost:8080/index.html

### 效果图如下

![输入图片说明](https://images.gitee.com/uploads/images/2018/1119/211831_c4cfd98b_1478371.png "屏幕截图.png")
