package com.project.XXcloud.Main;

import XMLUtil.XMLUtil;
import com.project.XXcloud.FileProcess.AnalyzeFile;
import com.project.XXcloud.SparkSense.SparkSense;
import org.apache.hadoop.conf.Configuration;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import java.util.List;

public class MainClass
{
    private static Configuration conf=new Configuration();
    private static String ip;
    private static String port;

    public static void main(String[] args) throws Exception {
        //inistialize spark
        SparkSense.initialSpark();
        try {
            String[] strings= XMLUtil.getHdfsConf();
            ip = strings[0];
            port=strings[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

        // Specify name server addresses.
        String namesrvAddr=XMLUtil.getRocktMQConf();
        consumer.setNamesrvAddr(namesrvAddr);

        // Subscribe one more more topics to consume.
        consumer.subscribe("FileMessage", "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                //System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
               for(MessageExt messageExt:msgs)
               {
                   try {
                       AnalyzeFile.analyze(ip,port,new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET),conf);
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
