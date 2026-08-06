package com.finnex.finance_app.storage.service.Impl;




import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finnex.finance_app.common.exceptions.ExternalServiceException;

import com.finnex.finance_app.storage.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File cannot be empty."
            );
        }

        try {

            String publicId = "transactions/"
                    + UUID.randomUUID();

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto"
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {

            throw new ExternalServiceException(
                    "Unable to upload file to Cloudinary. Error: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void deleteFile(String publicId) {

        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image"
                    )
            );

        } catch (IOException e) {

            throw new ExternalServiceException(
                    "Unable to delete file from Cloudinary. Error: "
                            + e.getMessage()
            );
        }
    }
}