package harmo.projects.shoppingcart.controller;

import harmo.projects.shoppingcart.dto.CartDto;
import harmo.projects.shoppingcart.dto.CartItemDto;
import harmo.projects.shoppingcart.dto.ProductDto;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Cart;
import harmo.projects.shoppingcart.response.ApiResponse;
import harmo.projects.shoppingcart.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {

    private final CartService cartService;
    private final ModelMapper modelMapper;
    @GetMapping("/{cartId}/my-cart")
    public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId){
        try {
            Cart cart = cartService.getCart(cartId);
            CartDto cartDto = modelMapper.map(cart, CartDto.class);
            Set<CartItemDto> collect = cart.getCartItems()
                    .stream()
                    .map(item -> {
                        CartItemDto cartItemDto = modelMapper.map(item, CartItemDto.class);
                        // map Product to ProductDto
                        ProductDto productDto = modelMapper.map(item.getProduct(), ProductDto.class);
                        // set ProductDto on CartItemDto
                        cartItemDto.setProductDto(productDto);
                        return cartItemDto;
                    })
                    .collect(Collectors.toSet());
            //Returning the cart dto instead of the actual cart.
            cartDto.setCartItems(collect);
            return ResponseEntity.ok(new ApiResponse("Success", cartDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId){
        try {
            cartService.clearCart(cartId);
            return ResponseEntity.ok(new ApiResponse("Clear cart Success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{cartId}/total-price")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId){
        try {
            BigDecimal totalPrice = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(new ApiResponse("Total Price", totalPrice));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
