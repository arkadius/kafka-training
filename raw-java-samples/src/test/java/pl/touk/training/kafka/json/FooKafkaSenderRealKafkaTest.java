package pl.touk.training.kafka.json;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class FooKafkaSenderRealKafkaTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(FooKafkaSenderRealKafkaTest.class);

    @Test
    public void shouldCommunicateUsingRealKafka() throws Exception {
        String bootstrapServers = "localhost:3032,localhost:3033,localhost:3034";
        String topic = "foo-topic";

        AtomicInteger consumedCount = new AtomicInteger();
        FooKafkaConsumer consumer = new FooKafkaConsumer(bootstrapServers, "foo.group") {
            @Override
            public void onRecords(ConsumerRecords<String, Foo> records, int threadId) {
                records.records(topic).forEach((r) -> {
                    LOGGER.info("Consumed: key:" + r.key() + ", value: " + r.value() + ", partition: " + r.partition());
                    consumedCount.incrementAndGet();
                });
            }
        };
        consumer.start(topic, 2);

        FooKafkaSender sender = new FooKafkaSender(bootstrapServers);
        sender.send(topic, new Foo("Bar", 18));
        sender.send(topic, new Foo("Baz", 21));

        await().atMost(Duration.ofSeconds(10)).untilAtomic(consumedCount, greaterThanOrEqualTo(2));
        consumer.stop();
    }

}
