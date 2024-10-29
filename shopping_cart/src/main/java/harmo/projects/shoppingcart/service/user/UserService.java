package harmo.projects.shoppingcart.service.user;

import harmo.projects.shoppingcart.dto.CartItemDto;
import harmo.projects.shoppingcart.dto.UserDto;
import harmo.projects.shoppingcart.model.Cart;
import harmo.projects.shoppingcart.model.User;
import harmo.projects.shoppingcart.request.CreateUserRequest;
import harmo.projects.shoppingcart.request.UserUpdateRequest;

import java.util.Set;

public interface UserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToUserDto(User user);

    Set<CartItemDto> getCartItemDtos(Cart cart);

    User getAuthenticatedUser();
}
