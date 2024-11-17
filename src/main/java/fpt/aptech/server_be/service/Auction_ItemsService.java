package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Auction_ItemsService {

    Auction_ItemsRepository auction_ItemsRepository;
    private final CategoryRepository categoryRepository;

    public Auction_ItemsResponse getAuction_ItemsById(Integer item_id) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(item_id).orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        return Auction_ItemsMapper.toAuction_ItemsResponse(auction_Items);
    }

    public List<Auction_ItemsResponse> getAllAuction_Items() {
        List<Auction_Items> auction_Items = auction_ItemsRepository.findAll();
        return auction_Items.stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList());
    }


    public void addAuction_Items(Auction_ItemsRequest request) {

        Auction_Items auction_Items = Auction_ItemsMapper.toAuction_Items(request);

        Category category = categoryRepository.findById(request.getCategory_id())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            auction_Items.setCategory(category);

        if (auction_Items.getBid_step() == null) {
            auction_Items.setBid_step("1");
        }

        if (auction_Items.getStatus() == null) {
            auction_Items.setStatus("ACTIVE");
        }

         auction_ItemsRepository.save(auction_Items);
    }

    public void updateAuction_Items(Auction_ItemsRequest request) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(request.getItem_id())
                .orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        if(!Objects.isNull(auction_Items)) {
            auction_Items.setItem_name(request.getItem_name());
            auction_Items.setDescription(request.getDescription());
            auction_Items.setImages(request.getImages());
            auction_Items.setStarting_price(request.getStarting_price());
            auction_Items.setStart_date(request.getStart_date());
            auction_Items.setEnd_date(request.getEnd_date());
            auction_Items.setBid_step(request.getBid_step());
            auction_Items.setStatus(request.getStatus());
        }

            Category category = categoryRepository.findById(request.getCategory_id())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            auction_Items.setCategory(category);

        auction_ItemsRepository.save(auction_Items);
    }

    public void deleteAuction_Items (Integer item_id){
        auction_ItemsRepository.deleteById(item_id);
    }

}
