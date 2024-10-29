package harmo.projects.shoppingcart.service.cart;

import harmo.projects.shoppingcart.model.CartItem;

public interface CartItemService {

    void addItemToCart(Long cartId, Long productId,int quantity);
    void removeItemFromCart(Long cartId, Long productId);

    CartItem getCartItem(Long productId, Long cartId);

    void updateItemQuantity(Long cartId, Long productId, int quantity);
}
