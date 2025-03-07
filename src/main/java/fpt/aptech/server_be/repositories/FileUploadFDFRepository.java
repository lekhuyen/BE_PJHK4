package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.FileUploadFDF;
import fpt.aptech.server_be.entities.ReviewItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadFDFRepository extends JpaRepository<FileUploadFDF, Integer> {

    @Query("SELECT r FROM FileUploadFDF r WHERE r.auctionItem.item_id = :auctionItemId")
    List<FileUploadFDF> findByAuctionItemId(int auctionItemId);
}
