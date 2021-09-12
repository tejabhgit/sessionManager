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
@ComponentScan(basePackages = "com.TicketService")
public class AppConfig {
	
	 static final String usageString =
	            "\nUsage: java -cp <classpath> NatsJsPushSubFilterSubject [-s server]"
	                    + "\n\nRun Notes:"
	                    + "\n   - THIS EXAMPLE IS NOT INTENDED TO BE CUSTOMIZED."
	                    + "\n     Supply the [-s server] value if your server is not at localhost:4222"
	                    + "\n\nUse tls:// or opentls:// to require tls, via the Default SSLContext\n"
	                    + "\nSet the environment variable NATS_NKEY to use challenge response authentication by setting a file containing your private key.\n"
	                    + "\nSet the environment variable NATS_CREDS to use JWT/NKey authentication by setting a file containing your user creds.\n"
	                    + "\nUse the URL for user/pass/token authentication.\n";
	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
		/*	Connection connect =  Nats.connect("nats://localhost:4222");

            JetStream js = connect.jetStream();
         // NoAck 1. Set up the noAck consumer / deliver subject configuration
            ConsumerConfiguration cc = ConsumerConfiguration.builder()
                    .ackPolicy(AckPolicy.None)
                    .ackWait(Duration.ofSeconds(1))
                    .build();

            PushSubscribeOptions pso = PushSubscribeOptions.builder()
                    .deliverSubject("RPSTicket")
                    .configuration(cc)
                    .build();

            // NoAck 2. Set up the JetStream and regular subscriptions
            //          Notice the JetStream subscribes to the real subject
            //          and the regular subscribes to the delivery subject
            //          Order matters, you must do the JetStream subscribe first
            JetStreamSubscription jsSub = js.subscribe("RPSTicket", pso);
            connect.flush(Duration.ofSeconds(5));
//            JetStreamSubscription sub = js.subscribe("RPSTicket");
            Message m = jsSub.nextMessage(Duration.ofSeconds(3));
            m.ack();
            System.out.println("Message: " + m.getSubject() + " " + new String(m.getData()));
            JsonUtils.printFormatted(m.metaData());
//
//            m = sub.nextMessage(Duration.ofSeconds(3));
//            m.ack();
//            System.out.println("Message: " + m.getSubject() + " " + new String(m.getData()));
//            JsonUtils.printFormatted(m.metaData());
 * 
*///
		//	Connection connect =  Nats.connect("nats://localhost:4222");			
		//	  ExampleArgs exArgs = ExampleArgs.builder().build(args, usageString);
			
			   String stream = "fs-strm-";
		        String subjectPrefix = "fs-sub-";
		        String subjectWild = subjectPrefix + ".*";
		        String subjectA = subjectPrefix + ".A";
		        String subjectB = subjectPrefix + ".B";
		        
		        try (Connection nc = Nats.connect("nats://localhost:4222")) {

		            JetStreamManagement jsm = nc.jetStreamManagement();

		            try {

		                // Create our JetStream context to publish and receive JetStream messages.
		                JetStream js = nc.jetStream();
		                // Explicit is the default Ack policy and means that each individual message must be acknowledged. For pull consumers, Explicit is the only allowed option.
		                // For push consumers, you can choose None, which means you do not have to ack any messages, or All which means whenever you choose to ack a message, all the previous messages received are automatically acknowledged. 
		                // You must ack within the Ack Wait window or it will be as if the ack was not received.
		                // 1. create a subscription that subscribes to the wildcard subject      
		                ConsumerConfiguration cc = ConsumerConfiguration.builder()
		                        .ackPolicy(AckPolicy.None) // don't want to worry about acking messages.
		                      //.filterSubject(subjectB)   // When consuming from a stream with a wildcard subject, the Filter Subject allows you to select a subset of the full wildcard subject to receive messages from.
		                        .ackWait(Duration.ofSeconds(1)) // Ack Wait is the time in nanoseconds that the server will wait for an ack for any individual message. If an ack is not received in time, the message will be redelivered.
		                        .build();

		                PushSubscribeOptions pso = PushSubscribeOptions.builder().configuration(cc).build();
		                JetStreamSubscription sub = js.subscribe(subjectWild, pso);
		                nc.flush(Duration.ofSeconds(5));

		                Message m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("1A1. Message should be from " + subjectA + ": " + m.getSubject()
		                        + " sequence # 1: " + m.metaData().streamSequence()+ " "+ new String(m.getData()));
		                
		                JsonUtils.printFormatted(m.metaData());
		                
		                m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("1B2. Message should be from " + subjectB + ": " + m.getSubject()
		                        + " sequence # 2: " + m.metaData().streamSequence()+ " "+ new String(m.getData()));
		                
		                JsonUtils.printFormatted(m.metaData());

		                m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("1A3. Message should be from " + subjectA + ": " + m.getSubject()
		                        + " sequence # 3: " + m.metaData().streamSequence() + " "+ new String(m.getData()));
		                
		                JsonUtils.printFormatted(m.metaData());

		                m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("1B4. Message should be from " + subjectB + ": " + m.getSubject()
		                        + " sequence # 4: " + m.metaData().streamSequence() + " "+ new String(m.getData()));
		                
		                JsonUtils.printFormatted(m.metaData());

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
		                        + " sequence # 1: " + m.metaData().streamSequence() + " "+ new String(m.getData()));
		                
		                JsonUtils.printFormatted(m.metaData());

		                m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("2A3. Message should be from " + subjectA + ": " + m.getSubject()
		                        + " sequence # 3: " + m.metaData().streamSequence() + " "+ new String(m.getData()));

		                JsonUtils.printFormatted(m.metaData());
		                
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
		                        + " sequence # 2: " + m.metaData().streamSequence() + " "+ new String(m.getData()));
		                JsonUtils.printFormatted(m.metaData());

		                m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("3A4. Message should be from " + subjectB + ": " + m.getSubject()
		                        + " sequence # 4: " + m.metaData().streamSequence() + " "+ new String(m.getData()));
		                JsonUtils.printFormatted(m.metaData());

		                m = sub.nextMessage(Duration.ofSeconds(1));
		                System.out.println("3x. Message should be null: " + m);
		            }
		            finally {
		                // be a good citizen and remove the example stream
		                jsm.deleteStream(stream);
		            }
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
			
			
			
			
		};
	}

}
