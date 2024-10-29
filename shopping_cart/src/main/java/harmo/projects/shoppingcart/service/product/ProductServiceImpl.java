package harmo.projects.shoppingcart.service.product;

import harmo.projects.shoppingcart.dto.ImageDto;
import harmo.projects.shoppingcart.dto.ProductDto;
import harmo.projects.shoppingcart.exceptions.AlreadyExistException;
import harmo.projects.shoppingcart.exceptions.ProductNotFoundException;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Category;
import harmo.projects.shoppingcart.model.Image;
import harmo.projects.shoppingcart.model.Product;
import harmo.projects.shoppingcart.repository.CategoryRepository;
import harmo.projects.shoppingcart.repository.ImageRepository;
import harmo.projects.shoppingcart.repository.ProductRepository;
import harmo.projects.shoppingcart.request.AddProductRequest;
import harmo.projects.shoppingcart.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Override
    public Product addProduct(AddProductRequest request) {
        //check if the category is found in the db
        //if yes, set it as the new product category
        // if No then save it as a new category then set it as the new Product category
        if(productExists(request.getName(), request.getBrand())){
            throw new AlreadyExistException(request.getBrand() + " "+
                    request.getName()+ " already exist, you may update this product instead!");
        }
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(()-> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name, String brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }
    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Product Not found."));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete,
                ()-> {throw new ProductNotFoundException("Product Not found.");});
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long product_id) {
        return productRepository.findById(product_id)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository ::save)
                .orElseThrow(()-> new ProductNotFoundException("Product Not found."));
    }
    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductByName(String product_name) {
        return productRepository.findByName(product_name);
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String product_name) {
        return productRepository.findByBrandAndName(brand, product_name);
    }

    @Override
    public Long countByBrandAndName(String brand, String product_name) {
        return productRepository.countByBrandAndName(brand, product_name);
    }
    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    @Override
    public ProductDto convertToDto(Product product){
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos =
                images.stream()
                        .map(image -> modelMapper.map(image, ImageDto.class))
                        .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
}
