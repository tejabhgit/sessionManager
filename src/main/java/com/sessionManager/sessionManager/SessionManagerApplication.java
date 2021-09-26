package com.sessionManager.sessionManager;



import com.sessionManager.sessionManager.nats.MessageResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import java.util.function.Consumer;
//import java.util.function.
import javax.xml.transform.Source;
import org.springframework.messaging.Message;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "svc-rps-support-session API", version = "2.0", description = "Support Session Information"))
public class SessionManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionManagerApplication.class, args);
	}

	@Bean
    public Consumer<Message<String>> notificationEventSupplier() {
		return msg -> {
	         System.out.println(msg);
	      };
 }
}