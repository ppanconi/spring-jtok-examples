package it.plansoft.ecommerce.order;

import com.jtok.spring.subscriber.ExternalDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderService {

    final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @EventListener(condition = "#event.name == 'payments.PAYMENTS_OPERATION_REFUSED' && #event.refName =='ecommerce.ORDER_TO_BE_PAYED'")
    public void handlePaymentRefused(ExternalDomainEvent event) {

        logger.info("handle event " + event);

        Optional<Order> byGlobalId = repository.findByGlobalId(event.getKey());
        if (byGlobalId.isPresent()) {

            Order order = byGlobalId.get();
            if (order.getStatus() == OrderStatus.TO_BE_PAYED) {

                order.setStatus(OrderStatus.PAYMENT_REFUSED);
                repository.save(order);
            }

        } else {
            // !!! report to evidence !!!
            // in production probably you should use
            // a alerting system with dead leter topic
            throw new RuntimeException("ref event but no present key");
        }
    }

    @Transactional
    @EventListener(condition = "#event.name == 'payments.PAYMENTS_OPERATION_ADDED' && #event.refName =='ecommerce.ORDER_TO_BE_PAYED'")
    public void handlePayment(ExternalDomainEvent event) {

        logger.info("handle event " + event);

        Optional<Order> byGlobalId = repository.findByGlobalId(event.getKey());
        if (byGlobalId.isPresent()) {

            Order order = byGlobalId.get();
            if (order.getStatus() == OrderStatus.TO_BE_PAYED) {

                order.setStatus(OrderStatus.PAYED);
                repository.save(order);
            }

        } else {
            // !!! report to evidence !!!
            // in production probably you should use
            // a alerting system with dead leter topic
            throw new RuntimeException("ref event but no present key");
        }
    }
}
