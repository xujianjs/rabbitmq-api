package com.xujianjian.rabbitmqapi.limit;

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

public class Consumer {

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
    String exchangeName = "test_qos_exchange";
    String exchangeType = BuiltinExchangeType.TOPIC.getType();
    String queueName = "test_qos_queue";
    String routingKey = "qos.*";//模糊单个单词
    channel.exchangeDeclare(exchangeName, exchangeType,true,false,false,null);

    //5、 声明队列
    channel.queueDeclare(queueName,false,false,false,null);
    //6、 通道绑定交换机和队列
    channel.queueBind(queueName, exchangeName, routingKey);


    //5、 声明队列消息的消费者【 如果通道中有消息 就通过回调机制读取消息】
    MyConsumer defaultConsumer = new MyConsumer(channel);

    // 限流 prefetchCount指定为1  每次只传递一条消息 配合手动签收  如果自定义consumer不实现签收功能 就会一直卡在那 队列中的其它消息就被“限制”住了
    channel.basicQos(0,1,false);
    // 限流 必须把自动应答关闭 由自己实现
    channel.basicConsume(queueName, false, defaultConsumer);

  }


}
