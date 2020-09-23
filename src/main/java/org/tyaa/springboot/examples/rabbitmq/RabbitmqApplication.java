package org.tyaa.springboot.examples.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitmqApplication {

	static final String topicExchangeName = "spring-boot-exchange";
	static final String queueName = "spring-boot";

	/* Создание очереди */
	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	/* Создание пункта обмена сообщениями с именем "spring-boot-exchange".
	* При подходе AMQP при необходимости отправлять сообщения только одному получателю
	* их сначала принимает этот пункт, а затем отправляет в очередь,
	* а если нужна рассылка многим получателям -
	* отсюда же сообщения отправляются в отдельные очереди */
	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}

	/* Связывание пункта обмена сообщениями с очередью */
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
	}

	/* Установка слушателя для приема сообщений из очереди "spring-boot".
	* Кроме этого бина и приемника, обрабатывающего сообщения (файл Receiver)
	* приложению-слушателю ничего больше не нужно. */
	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
											 MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	/* Регистрация слушателя сообщений */
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(RabbitmqApplication.class, args).close();
	}
}
