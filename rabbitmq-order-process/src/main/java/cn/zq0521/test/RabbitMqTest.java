package cn.zq0521.test;

import cn.zq0521.callbackstream.CallbackStreamService;
import cn.zq0521.downstream.DownStreamService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqTest {

    public static void main(String[] args) throws IOException, TimeoutException {
        DownStreamService downStreamService = new DownStreamService();
        downStreamService.ListenToOrderService();


        CallbackStreamService callbackStreamService = new CallbackStreamService();
        callbackStreamService.ListenToStockMessage();
    }
}
