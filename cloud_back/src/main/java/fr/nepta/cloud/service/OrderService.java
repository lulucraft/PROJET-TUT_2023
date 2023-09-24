package fr.nepta.cloud.service;

import fr.nepta.cloud.model.Order;

public interface OrderService {

	Order saveOrder(Order order);

	Order getOrder(String paypalId);

//	List<Order> getByUser(User user);

}
