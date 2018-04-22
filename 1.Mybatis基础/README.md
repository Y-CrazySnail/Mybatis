#  Mybatis基础



### 写在前面的话

本次Mybatis基础基于购买商品形成订单的业务，业务逻辑不严谨，重心在于如何使用Mybatis

<br>

### ER图

![](https://github.com/Y-CrazySnail/Mybatis/blob/master/Images/basics.png)

<br>

### 准备数据库

```mysql
-- 创建商品类别表--
use Mybatis;
create table category_ (
    id varchar(20) primary key,
    name varchar(20)
    );
insert into category_ values('001','数码');
insert into category_ values('002','食品');
-- 创建商品表--
use Mybatis;
create table product_(
    id varchar(20) primary key,
    name varchar(20),
    price float,
    cid varchar(20),
    foreign key(cid) references category_(id)
    );
insert into product_ values('001','iphone X',8888,'001');
insert into product_ values('002','牛奶',2.5,'002');
-- 创建订单表--
use Mybatis;
create table order_(
    id varchar(20) primary key,
    address varchar(100)
    );
insert into order_ values('001','北京');
insert into order_ values('002','上海');
-- 创建订单项目表--
use Mybatis;
create table order_item(
    number int,
    oid varchar(20),
    pid varchar(20),
    foreign key(oid) references order_(id),
    foreign key(pid) references product_(id)
    );
insert into order_item values(1,'001','001');
insert into order_item values(20,'001','002');
insert into order_item values(2,'002','001');
insert into order_item values(10,'002','002');
```

