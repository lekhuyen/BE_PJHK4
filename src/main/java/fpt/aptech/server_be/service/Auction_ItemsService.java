package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsRespone;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import fpt.aptech.server_be.mapper.Auction_ItemsMapper;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Auction_ItemsService {

    Auction_ItemsRepository auction_ItemsRepository;
    private final CategoryRepository categoryRepository;

    public Auction_ItemsRespone getAuction_ItemsById(Integer item_id) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(item_id).orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        return Auction_ItemsMapper.toAuction_ItemsRespone(auction_Items);
    }

    public List<Auction_ItemsRespone> getAllAuction_Items() {
        List<Auction_Items> auction_Items = auction_ItemsRepository.findAll();
        return auction_Items.stream().map(Auction_ItemsMapper::toAuction_ItemsRespone).collect(Collectors.toList());
    }

    public Auction_Items addAuction_Items(Auction_ItemsRequest auction_ItemsRequest) {

        Auction_Items auction_Items = Auction_ItemsMapper.toAuction_Items(auction_ItemsRequest);

        //User currentUser = getCurrentUser();
        //auction_Items.setUser(currentUser);

        // Set the Category based on the category_id from the request
        if (auction_ItemsRequest.getCategory_id() != null) {
            Category category = categoryRepository.findById(auction_ItemsRequest.getCategory_id())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            auction_Items.setCategory(category);
        }

        // Optional: Set default values for `bid_step` and `status`
        if (auction_Items.getBid_step() == null) {
            auction_Items.setBid_step("1"); // Default bid step
        }

        if (auction_Items.getStatus() == null) {
            auction_Items.setStatus("ACTIVE"); // Default status
        }


        return auction_ItemsRepository.save(auction_Items);
    }

    public boolean updateAuction_Items(Integer item_id, Auction_ItemsRequest request) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(item_id).orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        auction_Items.setItem_name(request.getItem_name());
        auction_Items.setDescription(request.getDescription());
        auction_Items.setImages(request.getImages());
        auction_Items.setStarting_price(request.getStarting_price());
        auction_Items.setStart_date(request.getStart_date());
        auction_Items.setEnd_date(request.getEnd_date());
        auction_Items.setBid_step(request.getBid_step());
        auction_Items.setStatus(request.getStatus());

        // Update the category only if category_id is provided
        if (request.getCategory_id() != null) {
            Category category = categoryRepository.findById(request.getCategory_id())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            auction_Items.setCategory(category);
        }
        Auction_Items auction_ItemsUpdated = auction_ItemsRepository.save(auction_Items);
        return auction_ItemsUpdated != null;
    }

    public void deleteAuction_Items (Integer item_id){
        auction_ItemsRepository.deleteById(item_id);
    }

}
