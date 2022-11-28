package pl.touk.training.kafka.avro;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class AvroRealKafkaTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(AvroRealKafkaTest.class);

    @Test
    public void shouldCommunicateUsingRealKafka() throws Exception {
        String bootstrapServers = "localhost:3032,localhost:3033,localhost:3034";
        String schemaRegistryUrl = "http://localhost:3082";
        String topic = "foo-topic";

        AtomicInteger consumedCount = new AtomicInteger();
        FooKafkaAvroConsumer consumer = new FooKafkaAvroConsumer(bootstrapServers, schemaRegistryUrl, "foo.group") {
            @Override
            public void onRecords(ConsumerRecords<String, Foo> records, int threadId) {
                records.records(topic).forEach((r) -> {
                    LOGGER.info("Consumed: key:" + r.key() + ", value: " + r.value() + ", partition: " + r.partition());
                    LOGGER.info("Name: " + r.value().getName());
                    consumedCount.incrementAndGet();
                });
            }
        };
        consumer.start(topic, 2);

        FooKafkaAvroSender sender = new FooKafkaAvroSender(bootstrapServers, schemaRegistryUrl);
        sender.send(topic, new Foo("Bar", 18));
        sender.send(topic, new Foo("Baz", 21));

        await().atMost(Duration.ofSeconds(30)).untilAtomic(consumedCount, greaterThanOrEqualTo(2));
        consumer.stop();
    }

}
