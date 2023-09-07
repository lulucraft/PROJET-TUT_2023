package fr.nepta.cloud.service;

import java.util.List;

import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.model.User;

public interface OrderService {

	Order saveOrder(Order order);

	Order getOrder(String paypalId);

//	List<Order> getByUser(User user);

}
