package com.sessionManager.sessionManager;


import java.time.Duration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamManagement;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.PublishAck;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import io.nats.client.support.JsonUtils;
import static com.sessionManager.sessionManager.NatsJsUtils.publish;
import static com.sessionManager.sessionManager.NatsJsUtils.createStream;
import static com.sessionManager.sessionManager.ExampleUtils.uniqueEnough;

@Configuration
public class AppConfig {

	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {

			String stream = "fs-strm-ticket-";
			String subjectPrefix = "fs-sub-ticket-";
			String subjectWild = subjectPrefix + ".*";
			String subjectA = subjectPrefix + ".A";
			String subjectB = subjectPrefix + ".B";
			try (Connection nc = Nats.connect("nats://localhost:4221")) {

				JetStreamManagement jsm = nc.jetStreamManagement();

				try {
					// creates a memory stream. We will clean it up at the end.
					createStream(jsm, stream, subjectWild);

					// Create our JetStream context to publish and receive JetStream messages.
					JetStream js = nc.jetStream();


					// 1. create a subscription that subscribes to the wildcard subject
					ConsumerConfiguration cc = ConsumerConfiguration.builder()
							.ackPolicy(AckPolicy.None) // don't want to worry about acking messages.
							.build();

					PushSubscribeOptions pso = PushSubscribeOptions.builder().configuration(cc).build();

					JetStreamSubscription sub = js.subscribe(subjectWild, pso);
					nc.flush(Duration.ofSeconds(5));

					Message m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("1A1. Message should be from " + subjectA + ": " + m.getSubject()
							+ " sequence # 1: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("1B2. Message should be from " + subjectB + ": " + m.getSubject()
							+ " sequence # 2: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("1A3. Message should be from " + subjectA + ": " + m.getSubject()
							+ " sequence # 3: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("1B4. Message should be from " + subjectB + ": " + m.getSubject()
							+ " sequence # 4: " + m.metaData().streamSequence());

					// 2. create a subscription that subscribes only to the A subject
					cc = ConsumerConfiguration.builder()
							.ackPolicy(AckPolicy.None) // don't want to worry about acking messages.
							.filterSubject(subjectA)
							.build();

					pso = PushSubscribeOptions.builder().configuration(cc).build();

					sub = js.subscribe(subjectWild, pso);
					nc.flush(Duration.ofSeconds(5));

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("2A1. Message should be from " + subjectA + ": " + m.getSubject()
							+ " sequence # 1: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("2A3. Message should be from " + subjectA + ": " + m.getSubject()
							+ " sequence # 3: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("2x. Message should be null: " + m);

					// 3. create a subscription that subscribes only to the A subject
					cc = ConsumerConfiguration.builder()
							.ackPolicy(AckPolicy.None) // don't want to worry about acking messages.
							.filterSubject(subjectB)
							.build();

					pso = PushSubscribeOptions.builder().configuration(cc).build();

					sub = js.subscribe(subjectWild, pso);
					nc.flush(Duration.ofSeconds(5));

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("3A2. Message should be from " + subjectB + ": " + m.getSubject()
							+ " sequence # 2: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("3A4. Message should be from " + subjectB + ": " + m.getSubject()
							+ " sequence # 4: " + m.metaData().streamSequence());

					m = sub.nextMessage(Duration.ofSeconds(1));
					System.out.println("3x. Message should be null: " + m);
				}
				finally {
					// be a good citizen and remove the example stream
					//   jsm.deleteStream(stream);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		};
	}
}