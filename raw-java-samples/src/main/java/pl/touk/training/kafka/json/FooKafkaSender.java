package pl.touk.training.kafka.json;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FooKafkaSender {
    private KafkaProducer<String, Foo> producer;

    public FooKafkaSender(String bootstrapServers) {
        producer = new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ));
    }

    public RecordMetadata send(String topic, Foo value) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topic, value.getName(), value));
        return future.get();
    }

}
