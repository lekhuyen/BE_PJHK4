package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.FileUploadDTO;
import fpt.aptech.server_be.entities.FileUploadFDF;
import fpt.aptech.server_be.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    // Get file by ID
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable int id) {
        System.out.println("Fetching file with ID: " + id);

        FileUploadFDF file = fileUploadService.getFile(id);
        if (file == null) {
            System.out.println("File not found!");
            return ResponseEntity.notFound().build();
        }

        System.out.println("Returning file: " + file.getFileName() + " (" + file.getFileType() + ")");

        return ResponseEntity.ok()
                .header("Content-Type", file.getFileType())
                .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getFileData());
    }

    @GetMapping("/auctionItem/{auctionItemId}")
    public ResponseEntity<byte[]> getFileByAuctionItem(@PathVariable int auctionItemId) {
        System.out.println("Fetching file for Auction Item ID: " + auctionItemId);

        FileUploadFDF file = fileUploadService.getFileByAuctionItemId(auctionItemId);
        if (file == null) {
            System.out.println("❌ File not found for Auction Item ID: " + auctionItemId);
            return ResponseEntity.notFound().build();
        }

        System.out.println("✅ Returning file: " + file.getFileName() + " (" + file.getFileType() + ")");

        return ResponseEntity.ok()
                .header("Content-Type", file.getFileType())
                .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getFileData());
    }




    // Upload a new file
    @PostMapping("/upload")
    public ResponseEntity<FileUploadDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "auctionItemId", required = false) Integer auctionItemId) {
        try {
            FileUploadDTO savedFile = fileUploadService.uploadFile(file, auctionItemId);
            return ResponseEntity.ok(savedFile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Update (Replace) file
    @PutMapping("/{id}")
    public ResponseEntity<FileUploadFDF> updateFile(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        try {
            FileUploadFDF updatedFile = fileUploadService.updateFile(id, file);
            if (updatedFile == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedFile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete file
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable int id) {
        boolean isDeleted = fileUploadService.deleteFile(id);
        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("File deleted successfully");
    }
}
