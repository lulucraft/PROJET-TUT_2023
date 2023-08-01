package fr.nepta.cloud.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.repository.OrderRepo;
import fr.nepta.cloud.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepo orderRepo;

	@Override
	public Order saveOrder(Order order) {
		log.info("Saving new order to the database");
		return orderRepo.save(order);
	}

	@Override
	public Order getOrder(String paypalId) {
		log.info("Fetching order '{}' from the database", paypalId);
		return orderRepo.findByPaypalId(paypalId);
	}

}
