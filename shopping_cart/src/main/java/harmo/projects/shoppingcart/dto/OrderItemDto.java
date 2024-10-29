package harmo.projects.shoppingcart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private String productName;
    private int quantity;
    private BigDecimal price;
    private Long productId;
}
