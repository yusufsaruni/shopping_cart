package harmo.projects.shoppingcart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private String productName;
    private String productBrand;
    private int quantity;
    private Long productId;
    private BigDecimal price;
}
