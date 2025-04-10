package co.portal.gateway;

import co.portal.gateway.service.RabbitMQService;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class GatewayApplication implements CommandLineRunner {
	@Autowired
	private RabbitMQService rabbitMQService;

	public GatewayApplication(RabbitMQService rabbitMQService) {
		this.rabbitMQService = rabbitMQService;
	}


	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);

	}


	@Override
	public void run(String... args) throws Exception {
//		rabbitMQService.sendTestMessage();
	}


	}
