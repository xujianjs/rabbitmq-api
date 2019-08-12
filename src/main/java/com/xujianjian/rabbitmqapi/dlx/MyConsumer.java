package com.xujianjian.rabbitmqapi.dlx;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

public class MyConsumer extends DefaultConsumer {

  private Channel channel;

  /**
   * Constructs a new instance and records its association to the passed-in channel.
   *
   * @param channel the channel to which this consumer is attached
   */
  public MyConsumer(Channel channel) {
    super(channel);
    this.channel = channel;
  }


  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
      byte[] body) throws IOException {
    long deliveryTag = envelope.getDeliveryTag();
    String exchange = envelope.getExchange();
    String routingKey = envelope.getRoutingKey();
    System.err.printf("deliveryTag =%d ; exchange=%s ;routingKey=%s;consumerTag=%s ;  %s \n",deliveryTag,exchange,routingKey,consumerTag,properties);

    //手动签收（确认接收）
//    channel.basicAck(deliveryTag,false);

  }
}
