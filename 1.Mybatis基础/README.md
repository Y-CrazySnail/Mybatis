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

<br>

### 准备jar包

使用maven，在pom.xml的```<dependencies>```标签中加入

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.5</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.44</version>
</dependency>
<!-- 为了防止maven不在target中不加载映射文件，在<build>标签内加入一下内容 -->
<resources>
    <resource>
        <directory>src/main/java</directory>
        <includes>
            <include>**/*.xml</include>
        </includes>
        <filtering>true</filtering>
    </resource>
</resources>
```

<br>

###  Mybatis-config.xml配置

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeAliases>
        <package name="com.pojo"/>
    </typeAliases>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/Mybatis?characterEncoding=UTF-8"/>
                <property name="username" value="<数据库名>"/>
                <property name="password" value="<数据库密码>"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="com/pojo/Order.xml"/>
    </mappers>
</configuration>
```

这是一个最简单的连接**mysql**数据库的xml配置文件

- ```<typeAliases>```：类型别名是为 Java 类型设置一个短的名字。它只和 XML 配置有关，存在的意义仅在于用来减少类完全限定名的冗余。```<typeAliases>```内有```<typeAlias>```和```<package>```两个标签，前者是单一设置Java Bean别名，后者会扫描包所在位置，自动加载Java Bean
- ```<dataSource>```中的type有两个类型**UNPOOLED**和**POOLED**。**UNPOOLED**数据源的实现只是每次被请求时打开和关闭连接；**POOLED**数据源的实现利用"池"的概念将 JDBC 连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。```<property>```中写入链接数据库需要的属性
- ```<mappers>```映射器：根据```<mapper>```定位映射文件

<br>

### 准备Java Bean

-  **Category.java**：商品类别Bean

```java
package com.pojo;

public class Category {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

-  **Product.java**：商品Bean

```java
package com.pojo;

public class Product {
    private String id;
    private String name;
    private float price;
    private Category category;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
```

-  **Order.java**：订单Bean

```java
package com.pojo;

import java.util.List;

public class Order {
    private String id;
    private String address;
    List<OrderItem> orderItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
```

-  **OrderItem.java**：订单项Bean

```java
package com.pojo;

public class OrderItem {
    private int number;
    private String id;
    private Product product;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
```

<br>

### 配置XML映射文件

- ```<resultMap>```是最复杂也是最强大的元素，用来描述如何从数据库结果集中来加载对象。

- ```<colloection>```和```<association>```的区别，前者是复杂类型的集，简单来说就是一对多关系；后者是一个复杂的类型关联;许多结果将包成这种类型，简单来说就是一对一关系。

-  **javaType和ofType**

  由于类型名重复导致的特殊处理：

  | 别名       | 映射的类型 |
  | ---------- | ---------- |
  | _byte      | byte       |
  | _long      | long       |
  | _short     | short      |
  | _int       | int        |
  | _integer   | int        |
  | _double    | double     |
  | _float     | float      |
  | _boolean   | boolean    |
  | string     | String     |
  | byte       | Byte       |
  | long       | Long       |
  | short      | Short      |
  | int        | Integer    |
  | integer    | Integer    |
  | double     | Double     |
  | float      | Float      |
  | boolean    | Boolean    |
  | date       | Date       |
  | decimal    | BigDecimal |
  | bigdecimal | BigDecimal |
  | object     | Object     |
  | map        | Map        |
  | hashmap    | HashMap    |
  | list       | List       |
  | arraylist  | ArrayList  |
  | collection | Collection |
  | iterator   | Iterator   |

-  **property**：property所对应的值是JavaBean中的值

-  **column**：column所对应的值是```<select>```中查询到的值

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.pojo">
    <resultMap id="orderBean" type="Order">
        <id column="oid" property="id"/>
        <result column="address" property="address"/>
        <collection property="orderItems" ofType="OrderItem">
            <result column="number" property="number"/>
            <association property="product" javaType="Product">
                <id column="pid" property="id"/>
                <result column="pname" property="name"/>
                <result column="price" property="price"/>
                <association property="category" javaType="Category">
                    <id column="cid" property="id"/>
                    <result column="cname" property="name"/>
                </association>
            </association>
        </collection>
    </resultMap>
    <select id="listOrder" resultMap="orderBean">
        select o.*,oi.*,p.*,c.*,
        o.id as 'oid',
        p.id as 'pid',
        p.name as 'pname',
        c.id as 'cid',
        c.name as 'cname'
        from order_ as o
        left join order_item as oi on o.id=oi.oid
        left join product_ as p on oi.pid=p.id
        left join category_ as c on p.cid=c.id
    </select>
</mapper>
```

<br>

### 测试代码

```java
package com.test;

import com.pojo.Category;
import com.pojo.Order;
import com.pojo.OrderItem;
import com.pojo.Product;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception{
        String resource = "mybatis-config.xml";
        InputStream inputStream=Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession=sqlSessionFactory.openSession();
        List<Order> orders=sqlSession.selectList("listOrder");
        System.out.println(orders);
        Iterator<Order> orderIterator=orders.iterator();
        while(orderIterator.hasNext()){
            Order order=orderIterator.next();
            System.out.println("订单编号："+order.getId());
            System.out.println("收货地址："+order.getAddress());
            List<OrderItem> orderItems=order.getOrderItems();
            Iterator<OrderItem> orderItemIterator=orderItems.iterator();
            System.out.println(orderItems);
            while (orderItemIterator.hasNext()){
                OrderItem orderItem=orderItemIterator.next();
                Product product=orderItem.getProduct();
                System.out.println("商品编号："+product.getId());
                System.out.println("商品名称："+product.getName());
                System.out.println("商品价格："+product.getPrice()+"元");
                System.out.println("商品数量："+orderItem.getNumber());
                Category category=product.getCategory();
                System.out.println("商品类别："+category.getName());
            }
            System.out.println("--------------------------------");
        }
    }
}
```

<br>

### 执行结果

![](https://github.com/Y-CrazySnail/Mybatis/blob/master/Images/result1.png)