package com.xujianjian.rabbitmqapi.dlx;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * x-dead-letter-exchange  死信队列
 *
 * 死信队列：没有被及时消费的消息存放的队列，消息没有被及时消费有以下几点原因：
 *
 * a.消息被拒绝（basic.reject/ basic.nack）并且不再重新投递 requeue=false
 *
 * b.TTL(time-to-live) 消息超时未消费
 *
 * c.达到最大队列长度
 *
 *
 */
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

    //4、 声明交换机(普通方式)
    String exchangeName = "test_dlx_exchange";
    String exchangeType = BuiltinExchangeType.TOPIC.getType();
    String queueName = "test_dlx_queue";
    String routingKey = "dlx.*";//模糊单个单词
    channel.exchangeDeclare(exchangeName, exchangeType,true,false,false,null);

    // 这个arguments要设置到声明队列上 队列上的消息没有被消费  会被转到死信队列交换机dlx.exchange 然后路由到队列 dlx.queue上
    Map<String, Object> arguments = new HashMap<>();
    arguments.put("x-dead-letter-exchange", "dlx.exchange");

    //5、 声明队列
    channel.queueDeclare(queueName,false,false,false,arguments);
    //6、 通道绑定交换机和队列
    channel.queueBind(queueName, exchangeName, routingKey);

    // 声明死信队列
    channel.exchangeDeclare("dlx.exchange", exchangeType, true, false, false, null);
    channel.queueDeclare("dlx.queue", false, false, false, null);
    channel.queueBind("dlx.queue", "dlx.exchange","#");


    //5、 声明队列消息的消费者【 如果通道中有消息 就通过回调机制读取消息】
    MyConsumer defaultConsumer = new MyConsumer(channel);

    // 限流 prefetchCount指定为1  每次只传递一条消息 配合手动签收  如果自定义consumer不实现签收功能 就会一直卡在那 队列中的其它消息就被“限制”住了
    channel.basicQos(0,1,false);
    // 限流 必须把自动应答关闭 由自己实现
    channel.basicConsume(queueName, false, defaultConsumer);

  }


}
