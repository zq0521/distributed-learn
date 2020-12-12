package org.apache.rocketmq.example.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 批量消息  消费者
 */
public class SplitBatchProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("BatchProducerGroupName");
        try {
            producer.setNamesrvAddr("192.168.1.155:9876");
            producer.start();

            // large batch
            String topic = "TopicTest";
            List<Message> messageList = new ArrayList<>(100 * 1000);
            for (int i = 0; i < 100 * 1000; i++) {
                messageList.add(new Message(topic, "Tag", "OrderID" + i, ("Hello world " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)));
            }

            //split the large batch into small ones:
            ListSplitter splitter = new ListSplitter(messageList);
            while (splitter.hasNext()) {
                List<Message> listItem = splitter.next();
                producer.send(listItem);
            }
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ListSplitter implements Iterator<List<Message>> {
    private int sizeLimit = 1000 * 1000;
    private final List<Message> messages;
    private int currIndex;

    public ListSplitter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean hasNext() {
        return currIndex < messages.size();
    }

    @Override
    public List<Message> next() {
        int nextIndex = currIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            int tmpSize = message.getTopic().length() + message.getBody().length;
            Map<String, String> properties = message.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                tmpSize += entry.getKey().length() + entry.getValue().length();
            }
            tmpSize = tmpSize + 20; //for log overhead
            if (tmpSize > sizeLimit) {
                //it is unexpected that single message exceeds the sizeLimit
                //here just let it go, otherwise it will block the splitting process
                if (nextIndex - currIndex == 0) {
                    //if the next sublist has no element, add this one and then break, otherwise just break
                    nextIndex++;
                }
                break;
            }
            if (tmpSize + totalSize > sizeLimit) {
                break;
            } else {
                totalSize += tmpSize;
            }

        }
        List<Message> subList = messages.subList(currIndex, nextIndex);
        currIndex = nextIndex;
        return subList;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not allowed to remove");
    }
}