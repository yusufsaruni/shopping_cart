package harmo.projects.shoppingcart.service.category;

import harmo.projects.shoppingcart.exceptions.AlreadyExistException;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Category;
import harmo.projects.shoppingcart.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category Not Found"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                .filter(c ->
                    categoryRepository.existsByName(category.getName()))
                .map(categoryRepository::save)
                .orElseThrow(()-> new AlreadyExistException(category.getName() + " Already exist"));
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(oldCategory -> {
                    oldCategory.setName(category.getName());
                    return categoryRepository.save(oldCategory);
                }).orElseThrow(()-> new ResourceNotFoundException("Category Not Found"));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete,
                        ()-> {throw new ResourceNotFoundException("Category Not Found");});
    }
}
