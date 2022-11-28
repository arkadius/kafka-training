package pl.touk.training.kafka.transaction;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

public abstract class FooTransactionalKafkaConsumer {

    private final String bootstrapServers;

    private final String groupId;

    private volatile boolean running;

    public FooTransactionalKafkaConsumer(String bootstrapServers, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
    }

    public void start(String topic, int threadsCount) {
        running = true;
        for (int i = 1; i <= threadsCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try (
                        KafkaConsumer<String, Foo> consumer = new KafkaConsumer<>(Map.of(
                                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                                JsonDeserializer.VALUE_DEFAULT_TYPE, Foo.class.getName(),
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false,
                                ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase()
                        ))
                ) {
                    consumer.subscribe(Collections.singletonList(topic));
                    while (running) {
                        ConsumerRecords<String, Foo> records = consumer.poll(Duration.ofSeconds(1));
                        onRecords(records, threadId);
                        consumer.commitSync();
                    }
                }
            }, "FooConsumer-" + threadId).start();
        }
    }

    public abstract void onRecords(ConsumerRecords<String, Foo> records, int threadId);

    public void stop() {
        running = false;
    }

}
