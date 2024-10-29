package harmo.projects.shoppingcart.service.cart;

import harmo.projects.shoppingcart.model.Cart;
import harmo.projects.shoppingcart.model.User;

import java.math.BigDecimal;

public interface CartService {

    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
