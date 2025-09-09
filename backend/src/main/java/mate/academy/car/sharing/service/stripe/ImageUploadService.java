package mate.academy.car.sharing.service.stripe;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    String uploadCarImage(MultipartFile imageFile);
}

