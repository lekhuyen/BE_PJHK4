package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Auction_ItemsRepository extends JpaRepository<Auction_Items, Integer> {
    public List<Auction_Items> getAuction_ItemsByCategory(Category category);
    Page<Auction_Items> findByCategory(Category category, Pageable pageable);
}
