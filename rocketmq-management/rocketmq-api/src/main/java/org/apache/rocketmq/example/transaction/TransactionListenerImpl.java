package org.apache.rocketmq.example.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 事务监听器
 */
public class TransactionListenerImpl implements TransactionListener {

    /**
     * 事务提交完成后执行该方法
     * 返回COMMIT_MESSAGE状态的消息会立即被消费者消费到
     * 返回ROLLBACK_MESSAGE状态的消息会被丢弃
     * 返回UNKNOWN状态的消息会由broker过一段时间再来查询事务的状态
     *
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String tags = msg.getTags();
        // TagA的消息会立即被消费者消费到
        if (StringUtils.contains(tags, "TagA")) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        // TagB的消息会被丢弃掉
        else if (StringUtils.contains(tags, "TagB")) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else {
            // 其他消息会等待Broker进行处理
            return LocalTransactionState.UNKNOW;
        }
    }

    /**
     * 在对UNKNOWN状态的消息进行状态回查时执行，返回结果和executeLocalTransaction()方法一样的
     *
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String tags = msg.getTags();
        // 如果是TagC的消息过一段时间会被消费者消费到
        if (StringUtils.contains(tags, "TagC")) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        // 如果是TagD的消息也会在状态回查时被丢弃掉
        else if (StringUtils.contains(tags, "TagD")) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else {
            // 剩余的TagE的消息会在多次状态回查后被丢弃掉
            return LocalTransactionState.UNKNOW;
        }
    }
}
