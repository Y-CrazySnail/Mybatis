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
