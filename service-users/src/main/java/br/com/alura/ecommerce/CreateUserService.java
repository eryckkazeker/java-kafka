package br.com.alura.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {

  private final Connection connection;

  CreateUserService() throws SQLException {
    String url = "jdbc:sqlite:target/users_database.db";
    this.connection = DriverManager.getConnection(url);
    try {
      connection.createStatement().execute("create table Users ("+
        "uuid varchar(200) primary key, " +
        "email varchar(200))");
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }
  public static void main(String[] args) throws SQLException {
    CreateUserService createUserService = new CreateUserService();
    try (KafkaService<Order> service = new KafkaService<>(CreateUserService.class.getSimpleName(),
          "ECOMMERCE_NEW_ORDER",
        createUserService::parse,
        Order.class,
        Map.of())) {
      service.run();
    }
  }

  private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
    System.out.println("------------------------------------------");
    System.out.println("Processing new order, checking new user");
    System.out.println(record.value());
    Order order = record.value();
    if(isNewUser(order.getEmail())) {
      System.out.println("Inserting user [email="+order.getEmail()+"] into database");
      insertNewUser(order.getEmail());
    }
    System.out.println("Order processed");
  }

  private void insertNewUser(String email) throws SQLException {
    PreparedStatement insert = connection.prepareStatement("insert into Users (uuid, email) values(?,?)");
    insert.setString(1, UUID.randomUUID().toString());
    insert.setString(2, email);
    insert.execute();
  }
  
  private boolean isNewUser(String email) throws SQLException {
    PreparedStatement exists = connection.prepareStatement("select uuid from users where email = ? limit 1");
    exists.setString(1, email);
    try {
      ResultSet result = exists.executeQuery();
      return !result.next();
    } catch (SQLException ex) {
      return true;
    }
  }
}
