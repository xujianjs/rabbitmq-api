package com.xujianjian.rabbitmqapi.exchange.direct;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer4DirectExchange {
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
    // 1、创建一个连接工厂  【并且给定配方】
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(HOST);
    connectionFactory.setPort(PORT);
    connectionFactory.setVirtualHost(VIRTUAL_HOST);
    connectionFactory.setUsername("root");
    connectionFactory.setPassword("root");

    //2、 通过工厂创建一个连接
    Connection connection = connectionFactory.newConnection();

    //3、 创建一个通道
    Channel channel = connection.createChannel();

    //4、 声明交换机
    String exchangeName = "test_direct_exchange";
    String exchangeType = BuiltinExchangeType.DIRECT.getType();
    String queueName = "test_direct_queue";
    String routingKey = "test.direct";
    channel.exchangeDeclare(exchangeName, exchangeType,true,false,false,null);

    //5、 声明队列
    channel.queueDeclare(queueName,false,false,false,null);
    //6、 通道绑定交换机和队列
    channel.queueBind(queueName, exchangeName, routingKey);


    //5、 声明队列消息的消费者【 如果通道中有消息 就通过回调机制读取消息】
    DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
          byte[] body) throws IOException {
//        super.handleDelivery(consumerTag, envelope, properties, body);
        //6、 通过rabbitmq封装的消息对象读取具体的消息
        String msg = new String(body, StandardCharsets.UTF_8);
        System.out.printf("接收到的消息是：%s \n",msg);

      }
    };

    // 把声明的消费者绑定到通道和队列
    // 自动回应队列应答 ack=true
    channel.basicConsume(queueName, true, defaultConsumer);

  }
}
