package com.xxxx;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class ProducerApp {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        Faker faker = new Faker();
        Random random = new Random();

        try {
            for (int i = 1; i <= 10000; i++) {
                String studentCd = "SV" + String.format("%05d", i);
                String studentName = faker.name().fullName();
                double studentMark = 5.0 + (random.nextDouble() * 5.0); // điểm từ 5.0 - 10.0
                Date dob = faker.date().birthday(18, 25);
                LocalDate localDob = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String studentClass = "L" + (100 + random.nextInt(5)); // L100 - L104

                String json = String.format(
                        "{\"studentCd\": \"%s\", \"studentName\": \"%s\", \"studentMark\": %.2f, \"dateOfBirth\": \"%s\", \"studentClass\": \"%s\"}",
                        studentCd, studentName, studentMark, localDob.toString(), studentClass
                );

                producer.send(new ProducerRecord<>("demo-topic", studentCd, json), (metadata, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        System.out.printf("Sent [%s] to topic: %s partition: %d%n", studentCd, metadata.topic(), metadata.partition());
                    }
                });

                Thread.sleep(10); // tránh bị bão request, có thể tăng giảm tuỳ vào tình huống
            }
            producer.flush();
        } finally {
            producer.close();
        }
    }
}