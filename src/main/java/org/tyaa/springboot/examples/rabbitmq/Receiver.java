package org.tyaa.springboot.examples.rabbitmq;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    // счетчик, сигнализирующий, что сообщение получено
    private CountDownLatch latch = new CountDownLatch(1);
    // потребитель сообщений, выводящий его текст в консоль
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
