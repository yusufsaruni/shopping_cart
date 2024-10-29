package harmo.projects.shoppingcart.service.order;

import harmo.projects.shoppingcart.dto.OrderDto;
import harmo.projects.shoppingcart.enums.OrderStatus;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Cart;
import harmo.projects.shoppingcart.model.Order;
import harmo.projects.shoppingcart.model.OrderItem;
import harmo.projects.shoppingcart.model.Product;
import harmo.projects.shoppingcart.repository.OrderRepository;
import harmo.projects.shoppingcart.repository.ProductRepository;
import harmo.projects.shoppingcart.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItems = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));
        Order save = orderRepository.save(order);

        //clearing the cart once the user place the order.
        cartService.clearCart(cart.getId());
        return save;
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems){
        return orderItems
                .stream()
                .map(orderItem ->
                        orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private Order createOrder(Cart cart){
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart
                .getCartItems()
                .stream()
                .map(cartItem ->{
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,product,cartItem.getQuantity(),
                            cartItem.getUnitPrice());
                })
                .toList();
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId){
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private OrderDto convertToDto(Order order){
        return modelMapper.map(order, OrderDto.class);
    }
}
