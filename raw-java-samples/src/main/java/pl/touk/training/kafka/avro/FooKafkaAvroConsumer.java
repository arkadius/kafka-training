package pl.touk.training.kafka.avro;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

public abstract class FooKafkaAvroConsumer {

    private final String bootstrapServers;

    private final String groupId;

    private String schemaRegistryUrl;

    private volatile boolean running;

    public FooKafkaAvroConsumer(String bootstrapServers, String schemaRegistryUrl, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
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
                                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                                KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl,
                                KafkaAvroDeserializerConfig.SCHEMA_REFLECTION_CONFIG, true,
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
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
