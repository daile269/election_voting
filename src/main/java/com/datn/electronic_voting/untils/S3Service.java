package com.datn.electronic_voting.untils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadFile(MultipartFile image) throws IOException;
}
