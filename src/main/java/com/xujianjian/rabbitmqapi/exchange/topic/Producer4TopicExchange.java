package com.xujianjian.rabbitmqapi.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer4TopicExchange {
  /**
   * rabbitmq的服务器地址
   */
  static final String HOST = "192.168.1.8";
  /**
   * rabbitmq的服务器开放端口
   */
  static final int PORT = 5672;
  /**
   * 设置连通rabbitmq的指定虚拟主机地址
   */
  static final String VIRTUAL_HOST = "/";

  public static void main(String[] args) throws IOException, TimeoutException {
    //1、开办一个连接工厂，并进行配置（产品配方）
    ConnectionFactory connectionFactory = new ConnectionFactory();

    connectionFactory.setHost(HOST);
    connectionFactory.setPort(PORT);
    connectionFactory.setVirtualHost(VIRTUAL_HOST);
    connectionFactory.setUsername("root");
    connectionFactory.setPassword("root");

    //2、通过连接工厂创建一个连接
    Connection connection = connectionFactory.newConnection();

    //3、打开一个通道
    Channel channel = connection.createChannel();

    //4、 声明一个交换机
    String exchangeName = "test_topic_exchange";

    //5、声明一个队列【direct_exchange模式下  投递消息 通过 "路由键" 匹配 消费者绑定的队列名】
    String routingKey1 = "user.save";
    String routingKey2 = "user.update";
    String routingKey3 = "user.delete";
    String routingKey4 = "user.delete.abc";


    //、利用通道向【交换机】发送（字节形式）消息，带上路由键（通过路由键找到对应的接收方）
    String s = "Hello rabbitmq！";
    channel.basicPublish(exchangeName, routingKey1, null, (s+routingKey1).getBytes());
    channel.basicPublish(exchangeName, routingKey2, null, (s+routingKey2).getBytes());
    channel.basicPublish(exchangeName, routingKey3, null, (s+routingKey3).getBytes());
    channel.basicPublish(exchangeName, routingKey4, null, (s+routingKey4).getBytes());

    //5、关闭连接
    channel.close();
    connection.close();
  }


}
