package pl.touk.training.kafka.transaction;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionRealKafkaTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionRealKafkaTest.class);

    @Test
    public void shouldReadCommittedMessagesOnly() throws Exception {
        // GIVEN
        String bootstrapServers = "localhost:3032,localhost:3033,localhost:3034";
        String topic = "foo-transaction-topic";

        ConcurrentLinkedQueue<Foo> consumed = new ConcurrentLinkedQueue<>();
        FooTransactionalKafkaConsumer consumer = new FooTransactionalKafkaConsumer(bootstrapServers, "foo.group") {
            @Override
            public void onRecords(ConsumerRecords<String, Foo> records, int threadId) {
                records.records(topic).forEach((r) -> {
                    LOGGER.info("Consumed: key:" + r.key() + ", value: " + r.value() + ", partition: " + r.partition());
                    consumed.add(r.value());
                });
            }
        };
        consumer.start(topic, 2);

        FooTransactionalKafkaSender sender = new FooTransactionalKafkaSender(bootstrapServers);
        Foo foo = new Foo("Bar-" + UUID.randomUUID(), 18);

        // WHEN
        sender.getProducer().beginTransaction();
        sender.send(topic, foo);
        // FAIL!!!
        sender.getProducer().abortTransaction();
        sender.getProducer().beginTransaction();
        sender.send(topic, foo);
        sender.getProducer().commitTransaction();

        // THEN
        Thread.sleep(30_000);
        assertEquals(1, consumed.stream().filter((el) -> el.equals(foo)).count());
        consumer.stop();
    }

}
