package cn.zq0521;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class OrderProcessApplication {


    public static void main(String[] args) {
        SpringApplication.run(OrderProcessApplication.class, args);
    }

}
