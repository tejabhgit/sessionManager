package com.sessionManager.sessionManager;
import io.nats.client.*;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class ExampleUtils {


    public static String getServer(String[] args) {
        if (args.length == 1) {
            return args[0];
        } else if (args.length == 2 && args[0].equals("-s")) {
            return args[1];
        }
        return Options.DEFAULT_URL;
    }

    public static Options createExampleOptions(String[] args) throws Exception {
        String server = getServer(args);
        return createExampleOptions(server, false);
    }

    public static Options createExampleOptions(String[] args, boolean allowReconnect) throws Exception {
        String server = getServer(args);
        return createExampleOptions(server, allowReconnect);
    }

    public static Options createExampleOptions(String server) throws Exception {
        return createExampleOptions(server, false);
    }

    public static Options createExampleOptions(String server, boolean allowReconnect) throws Exception {
        Options.Builder builder = new Options.Builder()
                .server(server)
                .connectionTimeout(Duration.ofSeconds(5))
                .pingInterval(Duration.ofSeconds(10))
                .reconnectWait(Duration.ofSeconds(1))
                .errorListener(new ErrorListener() {
                    public void exceptionOccurred(Connection conn, Exception exp) {
                        System.out.println("Exception " + exp.getMessage());
                    }

                    public void errorOccurred(Connection conn, String type) {
                        System.out.println("Error " + type);
                    }

                    public void slowConsumerDetected(Connection conn, Consumer consumer) {
                        System.out.println("Slow consumer");
                    }
                })
                .connectionListener((conn, type) -> System.out.println("Status change "+type));

            if (!allowReconnect) {
                builder = builder.noReconnect();
            } else {
                builder = builder.maxReconnects(-1);
            }

            if (System.getenv("NATS_NKEY") != null && System.getenv("NATS_NKEY") != "") {
                AuthHandler handler = new ExampleAuthHandler(System.getenv("NATS_NKEY"));
                builder.authHandler(handler);
            } else if (System.getenv("NATS_CREDS") != null && System.getenv("NATS_CREDS") != "") {
                builder.authHandler(Nats.credentials(System.getenv("NATS_CREDS")));
            }

        return builder.build();
    }

    // Publish:   [options] <subject> <message>
    // Request:   [options] <subject> <message>

    // Subscribe: [options] <subject> <msgCount>
    // Reply:     [options] <subject> <msgCount>

    public static ExampleArgs optionalServer(String[] args, String usageString) {
        ExampleArgs ea = new ExampleArgs(args, null, usageString);
        if (ea.containedUnknown) {
            usage(usageString);
        }
        return ea;
    }

    public static ExampleArgs expectSubjectAndMessage(String[] args, String usageString) {
        ExampleArgs ea = new ExampleArgs(args, ExampleArgs.Trail.MESSAGE, usageString);
        if (ea.containedUnknown || ea.message == null) {
            usage(usageString);
        }
        return ea;
    }

    public static ExampleArgs expectSubjectAndMsgCount(String[] args, String usageString) {
        ExampleArgs ea = new ExampleArgs(args, ExampleArgs.Trail.COUNT, usageString);
        if (ea.containedUnknown || ea.msgCount < 1) {
            usage(usageString);
        }
        return ea;
    }

    public static ExampleArgs expectSubjectQueueAndMsgCount(String[] args, String usageString) {
        ExampleArgs ea = new ExampleArgs(args, ExampleArgs.Trail.COUNT, usageString);
        if (ea.containedUnknown || ea.msgCount < 1) {
            usage(usageString);
        }
        return ea;
    }

    public static void sleep(long millis) {
        try {
            if (millis > 0) {
                Thread.sleep(millis);
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public static void sleepRandom(long millis) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(millis));
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private static void usage(String usageString) {
        System.out.println(usageString);
        System.exit(-1);
    }

    public static String uniqueEnough() {
        String hex = Long.toHexString(System.currentTimeMillis()).substring(6);
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < hex.length(); x++) {
            char c = hex.charAt(x);
            if (c < 58) {
                sb.append((char)(c+55));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Long.toHexString(ThreadLocalRandom.current().nextLong()));
        }
        return sb.toString();
    }
}
