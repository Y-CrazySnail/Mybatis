# Mybatis动态SQL



<a href="">1、if标签</a>

<a href="">2、where标签</a>

<a href=""></a>

<a href=""></a>

<a href=""></a>

<a href=""></a>

<a href=""></a>

<a href=""></a>



### 写在前面的话

在Mybatis基础的基础上学习Mybatis的动态SQL，此处不再重复搭建环境



### if标签

当不确定是否按name查询时，传统的JDBC需要写两个查询语句，Mybatis用**if标签**实现，只需要写一段查询语句

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.pojo">
    <resultMap id="productBean" type="Product">
        <id column="pid" property="id"/>
        <result column="pname" property="name"/>
        <result column="price" property="price"/>
        <association property="category" javaType="Category">
            <id column="oid" property="id"/>
            <result column="cname" property="name"/>
        </association>
    </resultMap>
    <select id="dynamicSQLIfListProduct" resultMap="productBean">
        select p.*,c.*,
        p.id as 'pid',
        p.name as 'pname',
        c.id as 'cid',
        c.name as 'cname'
        from product_ as p
        left join category_ as c on p.cid=c.id
        <if test="name!=null">
            where p.name like concat('%',#{name},'%')
        </if>
    </select>
</mapper>
```



**测试代码：**

```java
public class Test {
    public static void main(String[] args) throws Exception{
        String resource = "mybatis-config.xml";
        InputStream inputStream=Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession=sqlSessionFactory.openSession();
        dynamicSQLIfListProduct(sqlSession);
    }
    private static void dynamicSQLIfListProduct(SqlSession sqlSession){
        List<Product> products=sqlSession.selectList("dynamicSQLIfListProduct");
        Iterator<Product> productIterator=products.iterator();
        System.out.println("全部查询：");
        while(productIterator.hasNext()){
            Product product=productIterator.next();
            System.out.println(product.getId());
            System.out.println(product.getName());
            System.out.println(product.getPrice());
            System.out.println(product.getCategory().getName());
        }
        Map<String,Object> map=new HashMap<>();
        map.put("name","iphone");
        List<Product> dproducts=sqlSession.selectList("dynamicSQLIfListProduct",map);
        Iterator<Product> dproductIterator=dproducts.iterator();
        System.out.println("根据name条件模糊查询：");
        while(dproductIterator.hasNext()){
            Product dproduct=dproductIterator.next();
            System.out.println(dproduct.getId());
            System.out.println(dproduct.getName());
            System.out.println(dproduct.getPrice());
            System.out.println(dproduct.getCategory().getName());
        }
    }
}
```



**执行结果：**

```
全部查询：
001
iphone X
8888.0
数码
002
牛奶
2.5
食品
根据name条件模糊查询：
001
iphone X
8888.0
数码
```



### where标签

当不只**name**一个条件查询时，就会用到and将两个条件连接起来，此时只使用**if标签**无法实现，此处需要使用**where标签**

在xml中，大于用 ```&gt;``` 小于用```&lt;```

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.pojo">
    <resultMap id="productBean" type="Product">
        <id column="pid" property="id"/>
        <result column="pname" property="name"/>
        <result column="price" property="price"/>
        <association property="category" javaType="Category">
            <id column="oid" property="id"/>
            <result column="cname" property="name"/>
        </association>
    </resultMap>
    <select id="dynamicSQLWhereListProduct" resultMap="productBean">
        select p.*,c.*,
        p.id as 'pid',
        p.name as 'pname',
        c.id as 'cid',
        c.name as 'cname'
        from product_ as p
        left join category_ as c on p.cid=c.id
        <where>
            <if test="name!=null">
                and p.name like concat('%',#{name},'%')
            </if>
            <if test="price!=null">
                and p.price &lt; #{price}
            </if>
        </where>
    </select>
</mapper>
```



**测试代码：**

```java
public class Test {
    public static void main(String[] args) throws Exception{
        String resource = "mybatis-config.xml";
        InputStream inputStream=Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession=sqlSessionFactory.openSession();
        dynamicSQLWhereListProduct(sqlSession);
    }
    private static void dynamicSQLWhereListProduct(SqlSession sqlSession){
        Map<String,Object> map=new HashMap<>();
        map.put("name","牛");
        map.put("price",3);
        List<Product> products=sqlSession.selectList("dynamicSQLWhereListProduct",map);
        Iterator<Product> productIterator=products.iterator();
        while(productIterator.hasNext()){
            Product product=productIterator.next();
            System.out.println(product.getId());
            System.out.println(product.getName());
            System.out.println(product.getPrice());
            System.out.println(product.getCategory().getName());
        }
    }
}
```



**执行结果：**

```
002
牛奶
2.5
食品
```



### set标签

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.pojo">
    <resultMap id="productBean" type="Product">
        <id column="pid" property="id"/>
        <result column="pname" property="name"/>
        <result column="price" property="price"/>
        <association property="category" javaType="Category">
            <id column="oid" property="id"/>
            <result column="cname" property="name"/>
        </association>
    </resultMap>
    <update id="dynamicSQLSetUpdateProduct" parameterType="Product">
        update product_
        <set>
            <if test="name!=null">
                name=#{name}
            </if>
        </set>
        where id=#{id}
    </update>
</mapper>
```



**测试代码：**

```java
public class Test {
    public static void main(String[] args) throws Exception{
        String resource = "mybatis-config.xml";
        InputStream inputStream=Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession=sqlSessionFactory.openSession();
        dynamicSQLSetUpdateProduct(sqlSession);
    }
    private static void listProduct(SqlSession sqlSession){
        List<Product> products=sqlSession.selectList("listProduct");
        Iterator<Product> productIterator=products.iterator();
        while(productIterator.hasNext()){
            Product product=productIterator.next();
            System.out.println(product.getId());
            System.out.println(product.getName());
            System.out.println(product.getPrice());
            System.out.println(product.getCategory().getName());
        }
    }
    private static void dynamicSQLSetUpdateProduct(SqlSession sqlSession) {
        Product product = new Product();
        product.setId("002");
        product.setName("伊利牛奶");
        sqlSession.update("dynamicSQLSetUpdateProduct",product);
        sqlSession.commit();
        listProduct(sqlSession);
    }
}
```



**执行结果：**

```
001
iPhone X
8888.0
数码
002
伊利牛奶
2.5
食品
```



### trim标签

```<set>```标签单独使用时，也会遇到像使用```<if>```标签时多个```and```的困扰，此时可以使用```<trim>```标签来解决

```<prefix>```-前缀

```<suffix>```-后缀

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.pojo">
    <resultMap id="productBean" type="Product">
        <id column="pid" property="id"/>
        <result column="pname" property="name"/>
        <result column="price" property="price"/>
        <association property="category" javaType="Category">
            <id column="oid" property="id"/>
            <result column="cname" property="name"/>
        </association>
    </resultMap>
    <update id="dynamicSQLTrimSetUpdateProduct" parameterType="Product">
        update product_
        <trim prefix="set" suffixOverrides=",">
            <if test="name!=null">
                name=#{name},
            </if>
            <if test="price!=null">
                price=#{price},
            </if>
        </trim>
        where id=#{id}
    </update>
</mapper>
```



**测试代码：**

```java
public class Test {
    public static void main(String[] args) throws Exception{
        String resource = "mybatis-config.xml";
        InputStream inputStream=Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession=sqlSessionFactory.openSession();
        dynamicSQLSetUpdateProduct(sqlSession);
    }
    private static void listProduct(SqlSession sqlSession){
        List<Product> products=sqlSession.selectList("listProduct");
        Iterator<Product> productIterator=products.iterator();
        while(productIterator.hasNext()){
            Product product=productIterator.next();
            System.out.println(product.getId());
            System.out.println(product.getName());
            System.out.println(product.getPrice());
            System.out.println(product.getCategory().getName());
        }
    }
    private static void dynamicSQLTrimSetUpdateProduct(SqlSession sqlSession){
        Product product=new Product();
        product.setId("002");
        product.setName("牛奶");
        product.setPrice(Float.parseFloat("3.5"));
        sqlSession.update("dynamicSQLTrimSetUpdateProduct",product);
        sqlSession.commit();
        listProduct(sqlSession);
    }
}
```



**执行结果：**

```
001
iPhone X
8888.0
数码
002
牛奶
3.5
食品
```



### 