package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

  private final String orderId, email;
  private final BigDecimal amount;

  public Order(String orderId, String email, BigDecimal amount) {
    this.email = email;
    this.orderId = orderId;
    this.amount = amount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    StringBuilder sb =  new StringBuilder();
    sb.append("[Order ");
    sb.append(" orderId= ");
    sb.append(orderId);
    sb.append(" amount= ");
    sb.append(amount);
    sb.append(" email= ");
    sb.append(email);
    sb.append("]");
    return sb.toString();
  }
    
}
