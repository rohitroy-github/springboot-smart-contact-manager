package springdev.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    public String uploadContactImage(MultipartFile contactImage);

}
