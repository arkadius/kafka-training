package pl.touk.training.kafka.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class PlainAvroTest {

    @Test
    public void shouldSerializeRecord() throws Exception {
        Schema schema = SchemaBuilder.record("Foo")
                .fields()
                .name("name").type(Schema.create(Schema.Type.STRING)).noDefault()
                .name("age").type(Schema.create(Schema.Type.INT)).noDefault()
                .endRecord();
        GenericRecord record = new GenericRecordBuilder(schema)
                .set("name", "Jan")
                .set("age", 12)
                .build();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DatumWriter writer = GenericData.get().createDatumWriter(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(bos, null);
        writer.write(record, encoder);
        encoder.flush();
        System.out.println(HexFormat.of().formatHex(bos.toByteArray()));
        System.out.println(bos.toString(StandardCharsets.US_ASCII));
    }

}
