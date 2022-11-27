package pl.touk.training.kafka.json;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

public abstract class FooKafkaConsumer {

    private volatile boolean running;
    private final KafkaConsumer<String, Foo> consumer;

    public FooKafkaConsumer(String bootstrapServers) {
        consumer = new KafkaConsumer<String, Foo>(Map.of(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.VALUE_DEFAULT_TYPE, Foo.class.getName(),
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "foo.group",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
        ));
    }

    public void start(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        running = true;
        new Thread() {
            @Override
            public void run() {
                while (running) {
                    ConsumerRecords<String, Foo> records = consumer.poll(Duration.ofSeconds(1));
                    onRecords(records);
                    consumer.commitSync();
                }
            }
        }.start();
    }

    public abstract void onRecords(ConsumerRecords<String, Foo> records);

    public void stop() {
        running = false;
    }

}
