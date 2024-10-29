package harmo.projects.shoppingcart.service.product;

import harmo.projects.shoppingcart.dto.ProductDto;
import harmo.projects.shoppingcart.model.Category;
import harmo.projects.shoppingcart.model.Product;
import harmo.projects.shoppingcart.request.AddProductRequest;
import harmo.projects.shoppingcart.request.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    Product addProduct(AddProductRequest request);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(UpdateProductRequest request, Long product_id);
    List<Product> getProductByCategory(String category);
    List<Product> getProductByBrand(String brand);

    List<Product> getProductByCategoryAndBrand(String category, String brand);
    List<Product> getProductByName(String product_name);
    List<Product> getProductByBrandAndName(String brand, String product_name);
    Long countByBrandAndName(String brand, String product_name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}
