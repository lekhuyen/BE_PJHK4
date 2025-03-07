package fpt.aptech.server_be.services;

import fpt.aptech.server_be.dto.request.FileUploadDTO;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.FileUploadFDF;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.FileUploadFDFRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadFDFRepository fileUploadFDFRepository;

    @Autowired
    private Auction_ItemsRepository auction_ItemsRepository;

    // Upload a new file
    public FileUploadDTO uploadFile(MultipartFile file, Integer auctionItemId) throws IOException {
        FileUploadFDF uploadedFile = FileUploadFDF.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileData(file.getBytes())
                .createdAt(new java.util.Date())
                .build();

        // Link to AuctionItem if provided
        if (auctionItemId != null) {
            Auction_Items auctionItem = auction_ItemsRepository.findById(auctionItemId)
                    .orElseThrow(() -> new RuntimeException("Auction Item Not Found"));
            uploadedFile.setAuctionItem(auctionItem);
        }

        FileUploadFDF savedFile = fileUploadFDFRepository.save(uploadedFile);

        // Convert to DTO (without fileData)
        return new FileUploadDTO(
                savedFile.getId(),
                savedFile.getFileName(),
                savedFile.getFileType(),
                savedFile.getCreatedAt(),
                savedFile.getAuctionItem()
        );
    }


    // Get a file by ID
    public FileUploadFDF getFile(int id) {
        return fileUploadFDFRepository.findById(id).orElse(null);
    }

    // Update an existing file
    public FileUploadFDF updateFile(int id, MultipartFile file) throws IOException {
        Optional<FileUploadFDF> existingFileOpt = fileUploadFDFRepository.findById(id);
        if (existingFileOpt.isPresent()) {
            FileUploadFDF existingFile = existingFileOpt.get();
            existingFile.setFileName(file.getOriginalFilename());
            existingFile.setFileType(file.getContentType());
            existingFile.setFileData(file.getBytes());
            return fileUploadFDFRepository.save(existingFile);
        }
        return null;
    }

    // Delete a file
    public boolean deleteFile(int id) {
        if (fileUploadFDFRepository.existsById(id)) {
            fileUploadFDFRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public FileUploadFDF getFileByAuctionItemId(int auctionItemId) {
        List<FileUploadFDF> files = fileUploadFDFRepository.findByAuctionItemId(auctionItemId);
        if (files.isEmpty()) {
            return null;
        }
        return files.get(0); // Return the first file (adjust if needed)
    }

}
