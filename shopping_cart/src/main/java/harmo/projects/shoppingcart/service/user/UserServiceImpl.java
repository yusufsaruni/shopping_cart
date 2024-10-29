package harmo.projects.shoppingcart.service.user;

import harmo.projects.shoppingcart.dto.*;
import harmo.projects.shoppingcart.exceptions.AlreadyExistException;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Cart;
import harmo.projects.shoppingcart.model.User;
import harmo.projects.shoppingcart.repository.UserRepository;
import harmo.projects.shoppingcart.request.CreateUserRequest;
import harmo.projects.shoppingcart.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    return userRepository.save(user);
                })
                .orElseThrow(()-> new AlreadyExistException("Oops! "+request.getEmail() + " Email Already Exist."));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastName(request.getLastName());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(
                        userRepository::delete,
                        ()-> {
                            throw new ResourceNotFoundException("User not found");
                        }
                );
    }

    @Override
    public UserDto convertUserToUserDto(User user){
        UserDto userDto = modelMapper.map(user, UserDto.class);
        // Mapping CartItem and Product for each item in the user's cart
        Cart cart = user.getCart();
        if(cart != null) {
            log.info("Cart is not null.");
            Set<CartItemDto> cartItemDtos = getCartItemDtos(cart);
            userDto.getCart().setCartItems(cartItemDtos);
        }
        // Mapping the Order to OrderDto(I wrote this -Orders to OrderDtos- using your example above(CartItem and Product mapping) Thanks a lot!
        List<OrderDto> orderDtos = user.getOrders().stream()
                .map(order -> {
                    List<OrderItemDto> orderItemDtos = order.getOrderItems()
                            .stream()
                            .map(orderItem -> modelMapper.map(orderItem, OrderItemDto.class)).toList();
                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                    orderDto.setOrderItems(orderItemDtos);
                    return orderDto;
                }).toList();
        //Setting the mapped OrderDtos in the userDto
        userDto.setOrders(orderDtos);
        return userDto;
    }

    @Override
    public Set<CartItemDto> getCartItemDtos(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    //Mapping the cartItem to cartItemDto
                    CartItemDto cartItemDto = modelMapper.map(cartItem, CartItemDto.class);
                    // Mapping the  Product to ProductDto
                    ProductDto productDto = modelMapper.map(cartItem.getProduct(), ProductDto.class);
                    // Setting the ProductDto on CartItemDto
                    cartItemDto.setProduct(productDto);
                    return cartItemDto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}
