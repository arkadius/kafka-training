package pl.touk.training.kafka.json;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;

class FooKafkaSenderRealKafkaTest {

    @Test
    public void shouldSendFooUsingRealKafka() throws Exception {
        String bootstrapServers = "localhost:3032,localhost:3033,localhost:3034";
        String topic = "foo-topic";

        AtomicInteger consumedCount = new AtomicInteger();
        FooKafkaConsumer consumer = new FooKafkaConsumer(bootstrapServers) {
            @Override
            public void onRecords(ConsumerRecords<String, Foo> records) {
                records.records(topic).forEach((r) -> {
                    System.out.println("Consumed: key:" + r.key() + ", value: " + r.value());
                    consumedCount.incrementAndGet();
                });
            }
        };
        consumer.start(topic);

        FooKafkaSender sender = new FooKafkaSender(bootstrapServers);
        sender.send(topic, new Foo("Bar", 18));

        await().untilAtomic(consumedCount, greaterThan(0));
        consumer.stop();
    }

}
