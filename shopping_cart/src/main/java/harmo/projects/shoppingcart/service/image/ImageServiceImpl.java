package harmo.projects.shoppingcart.service.image;

import harmo.projects.shoppingcart.dto.ImageDto;
import harmo.projects.shoppingcart.exceptions.ResourceNotFoundException;
import harmo.projects.shoppingcart.model.Image;
import harmo.projects.shoppingcart.model.Product;
import harmo.projects.shoppingcart.repository.ImageRepository;
import harmo.projects.shoppingcart.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(
                "No Image found with id: " + id
        ));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(
                imageRepository::delete,
                ()->{throw new ResourceNotFoundException("No Image found with id: " + id);
        });
    }

    @Override
    public List<ImageDto> saveImage(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file : files) {
            try{
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setProduct(product);
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));

                String downloadUrl = "/api/v1/images/image/download/"+ image.getId();
                image.setDownloadUrl(downloadUrl);
                Image saveImage = imageRepository.save(image);

                saveImage.setDownloadUrl("/api/v1/images/image/download/"+ image.getId());
                imageRepository.save(saveImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(saveImage.getId());
                imageDto.setFileName(file.getOriginalFilename());
                imageDto.setDownloadUrl(saveImage.getDownloadUrl());
                savedImageDto.add(imageDto);
            }catch (IOException | SQLException e){
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
