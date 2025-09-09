package mate.academy.car.sharing.service.impl;

import mate.academy.car.sharing.service.ImageUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {
    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public String uploadImage(MultipartFile image) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String path = UPLOAD_DIR + filename;
            Files.write(Paths.get(path), image.getBytes());

            return "/" + path;
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }
}
