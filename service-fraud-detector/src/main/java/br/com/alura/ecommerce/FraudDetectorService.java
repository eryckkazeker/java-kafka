package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {
  private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

  public static void main(String[] args) {
    FraudDetectorService fraudService = new FraudDetectorService();
    try (KafkaService<Order> service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
          "ECOMMERCE_NEW_ORDER",
        fraudService::parse,
        Order.class,
        Map.of())) {
      service.run();
    }
  }

  private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
    System.out.println("------------------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        // ignoring
        e.printStackTrace();
    }
    Order order = record.value();
    if (isFraud(order)) {
      System.out.println("FRAUD DETECTED!!!!");
      orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order);
    } else {
      System.out.println("Order Approved!");
      orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order);
    }
    System.out.println("Order processed");
  }

  private boolean isFraud(Order order) {
    return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
  }

}
