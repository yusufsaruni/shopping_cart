package harmo.projects.shoppingcart.controller;

import harmo.projects.shoppingcart.dto.OrderDto;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Order;
import harmo.projects.shoppingcart.response.ApiResponse;
import harmo.projects.shoppingcart.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> createOrder(Long userId) {
        try {
            Order order = orderService.placeOrder(userId);
            OrderDto createOrder = modelMapper.map(order, OrderDto.class);
            return ResponseEntity.ok(new ApiResponse("Order Successfully Placed!", createOrder));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error Occurred!", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}/order")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrder(orderId);

            return ResponseEntity.ok(new ApiResponse("Order Successfully Retrieved!", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("Error Occurred!", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Order Successfully Retrieved!", orders));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("Error Occurred!", e.getMessage()));
        }
    }
}
