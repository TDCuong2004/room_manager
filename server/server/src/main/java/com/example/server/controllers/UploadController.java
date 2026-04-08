package com.example.server.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UploadController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {

        try {
            // ✅ PATH CHUẨN (KHÔNG BAO GIỜ LỖI)
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 👉 TẠO FOLDER
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // ✅ DÙNG PATH TUYỆT ĐỐI
            File dest = new File(uploadDir + fileName);

            file.transferTo(dest);

            return "http://localhost:3000/uploads/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Upload failed");
        }
    }
}