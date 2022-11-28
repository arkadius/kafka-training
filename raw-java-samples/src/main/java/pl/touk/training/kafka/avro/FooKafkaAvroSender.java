package pl.touk.training.kafka.avro;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FooKafkaAvroSender {
    private KafkaProducer<String, Foo> producer;

    public FooKafkaAvroSender(String bootstrapServers, String schemaRegistryUrl) {
        producer = new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl,
                KafkaAvroSerializerConfig.SCHEMA_REFLECTION_CONFIG, true,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class
        ));
    }

    public RecordMetadata send(String topic, Foo value) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topic, value.getName(), value));
        return future.get();
    }

}
