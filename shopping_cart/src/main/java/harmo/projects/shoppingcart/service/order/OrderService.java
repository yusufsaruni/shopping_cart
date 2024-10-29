package harmo.projects.shoppingcart.service.order;

import harmo.projects.shoppingcart.dto.OrderDto;
import harmo.projects.shoppingcart.model.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);
}
