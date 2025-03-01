package fpt.aptech.server_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.dto.response.NotificationAuctionItemResponse;
import fpt.aptech.server_be.dto.response.PageResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import fpt.aptech.server_be.entities.NotificationAuctionItem;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.Auction_ItemsMapper;
import fpt.aptech.server_be.mapper.NotificationAuctionItemMapper;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.CategoryRepository;
import fpt.aptech.server_be.repositories.NotificationAuctionItemRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Auction_ItemsService {
    Cloudinary cloudinary;

    Auction_ItemsRepository auction_ItemsRepository;
    NotificationAuctionItemRepository notificationAuctionItemRepository;
    private final CategoryRepository categoryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    UserRepository userRepository;



    public Auction_ItemsResponse getAuction_ItemsById(Integer item_id) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(item_id).orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        return Auction_ItemsMapper.toAuction_ItemsResponse(auction_Items);
    }
//update isSell
    public boolean updateIsSell(Integer item_id) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(item_id).orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        auction_Items.setSell(true);
        auction_ItemsRepository.save(auction_Items) ;
        return true;
    }

    public PageResponse<Auction_ItemsResponse> getAllAuction_Items(int page, int size, String name) {

        if (page == 0 && size == 0) {
            Page<Auction_Items> auctionItems = null;

            if (name != null && !name.isEmpty()) {
                PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("updatedAt").descending());
                auctionItems = auction_ItemsRepository.findAllByItem_name(name, pageable);
            }
            else {
                PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("updatedAt").descending());
                auctionItems = auction_ItemsRepository.findAllS(pageable);
            }

            return PageResponse.<Auction_ItemsResponse>builder()
                    .currentPage(1)
                    .pageSize(auctionItems.getSize())
                    .totalPages(1)
                    .totalElements(auctionItems.getSize())
                    .data(auctionItems.stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList()))
                    .build();
        }

        // Phân trang với sort
        Sort sort = Sort.by("updatedAt").descending();
        PageRequest pageable = PageRequest.of(page - 1, size, sort);

        Page<Auction_Items> auctionItemsPage;

        //co name
        if (name != null && !name.isEmpty()) {
            auctionItemsPage = auction_ItemsRepository.findAllByItem_name(name, pageable);
        } else {
            auctionItemsPage = auction_ItemsRepository.findAllss(pageable);
        }

        return PageResponse.<Auction_ItemsResponse>builder()
                .currentPage(page)
                .pageSize(auctionItemsPage.getSize())
                .totalPages(auctionItemsPage.getTotalPages())
                .totalElements(auctionItemsPage.getTotalElements())
                .data(auctionItemsPage.getContent().stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList()))
                .build();
    }


//    lay sp chua ban
    public List<Auction_ItemsResponse> getAllAuction_ItemsBidding(){
        List<Auction_Items> auctionItems = auction_ItemsRepository.findAllProductBidding();
        return auctionItems.stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList());
    }

//    get all by user
    public List<Auction_ItemsResponse> getAllByCreator(String creator) {
        User user = userRepository.findById(creator).orElseThrow(() -> new RuntimeException("User is not found"));
        List<Auction_Items> auctionItems = auction_ItemsRepository.findAllByUser(user);
        return auctionItems.stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList());
    }

    public List<Auction_ItemsResponse> getAllByBuyer(String buyer) {
        User user = userRepository.findById(buyer).orElseThrow(() -> new RuntimeException("User is not found"));
        List<Auction_Items> auctionItems = auction_ItemsRepository.findAllBuyer(user);
        return auctionItems.stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList());
    }

//=============================================================================== k dc xoa
    public PageResponse<Auction_ItemsResponse> getAllAuction_Itemsssss(int page, int size) {

        if (page == 0 && size == 0) {
            List<Auction_Items> auctionItems = auction_ItemsRepository.findAll(Sort.by("updatedAt").descending());
            return PageResponse.<Auction_ItemsResponse>builder()
                    .currentPage(1)
                    .pageSize(auctionItems.size())
                    .totalPages(1)
                    .totalElements(auctionItems.size())
                    .data(auctionItems.stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList()))
                    .build();
        }

//size => so luong item tren 1 trang
        Sort sort = Sort.by( "updatedAt").descending();
        PageRequest pageable = PageRequest.of(page - 1, size,sort);

        Page<Auction_Items> auctionItemsPage = auction_ItemsRepository.findAll(pageable);




//        auction_Items.sort(Comparator.comparing(Auction_Items::getUpdatedAt).reversed());
//        return auction_Items.stream()
//                .sorted(Comparator.comparing(Auction_Items::getUpdatedAt).reversed())
//                .map(Auction_ItemsMapper::toAuction_ItemsResponse)
//                .collect(Collectors.toList());
        return PageResponse.<Auction_ItemsResponse>builder()
                .currentPage(page)
                //so luong sp moi trang
                .pageSize(auctionItemsPage.getSize())
                .totalPages(auctionItemsPage.getTotalPages())
                .totalElements(auctionItemsPage.getTotalElements())
                .data(auctionItemsPage.getContent().stream().map(Auction_ItemsMapper::toAuction_ItemsResponse).collect(Collectors.toList()))
                .build();
    }

    //get product by category
    public PageResponse<Auction_ItemsResponse> getAuctionItemByCategory(int category_id, Integer page, Integer size) {
        Category category = categoryRepository.findById(category_id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (page == null || size == null || (page == 0 && size == 0)) {
            List<Auction_Items> auctionItems = auction_ItemsRepository.getAuction_ItemsByCategoryId(category);
            auctionItems.sort((item1, item2) -> item2.getUpdatedAt().compareTo(item1.getUpdatedAt()));
            return PageResponse.<Auction_ItemsResponse>builder()
                    .currentPage(1)
                    .pageSize(auctionItems.size())
                    .totalPages(1)
                    .totalElements(auctionItems.size())
                    .data(auctionItems.stream()
                            .map(Auction_ItemsMapper::toAuction_ItemsResponse)
                            .collect(Collectors.toList()))
                    .build();
        }

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("updatedAt").descending());
        Page<Auction_Items> auctionItemsPage = auction_ItemsRepository.findByCategory(category, pageable);

        return PageResponse.<Auction_ItemsResponse>builder()
                .currentPage(page)
                .pageSize(auctionItemsPage.getSize())
                .totalPages(auctionItemsPage.getTotalPages())
                .totalElements(auctionItemsPage.getTotalElements())
                .data(auctionItemsPage.getContent().stream()
                        .map(Auction_ItemsMapper::toAuction_ItemsResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<Auction_ItemsResponse> getFeaturedAuctions() {
        List<Auction_Items> featuredItems = auction_ItemsRepository.findByIsSellTrue();
        return featuredItems.stream()
                .map(Auction_ItemsMapper::toAuction_ItemsResponse)
                .collect(Collectors.toList());
    }

    public List<Auction_ItemsResponse> getUpcomingAuctions() {
        List<Auction_Items> upcomingItems = auction_ItemsRepository.findByIsSellFalse();
        return upcomingItems.stream()
                .map(Auction_ItemsMapper::toAuction_ItemsResponse)
                .collect(Collectors.toList());
    }




    public void addAuction_Items(Auction_ItemsRequest request) {

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        List<MultipartFile> images = request.getImages();

        List<String> fileNames = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = image.getOriginalFilename();

            if(fileName != null && !fileName.isEmpty()) {
                try {
                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                    String fileUrl = uploadResult.get("url").toString();

                    fileNames.add(fileUrl);
                } catch (Exception e) {
                    throw new RuntimeException("Error uploading image: " + fileName, e);
                }
            }
        }
        Auction_Items auction_Items = Auction_ItemsMapper.toAuction_Items(request);
        auction_Items.setImages(fileNames);


        Category category = categoryRepository.findById(request.getCategory_id())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            auction_Items.setCategory(category);
         auction_ItemsRepository.save(auction_Items);


//         thong bao
        NotificationAuctionItem notificationAuctionItem = new NotificationAuctionItem();
        notificationAuctionItem.setAuctionItemId(auction_Items.getItem_id());
        notificationAuctionItem.setCreatedAt(new Date());
        notificationAuctionItem.setUpdatedAt(new Date());
        notificationAuctionItem.setType("P");
        notificationAuctionItem.setCreator(user);

        notificationAuctionItemRepository.save(notificationAuctionItem);

        NotificationAuctionItemResponse notificationAuctionItemResponse = NotificationAuctionItemMapper
                .toNotificationAuctionItemResponse(notificationAuctionItem);
        messagingTemplate.convertAndSend("/topic/notification/product", notificationAuctionItemResponse);


    }

    public void updateAuction_Items(Auction_ItemsRequest request) throws IOException {
        Auction_Items auction_Items = auction_ItemsRepository.findById(request.getItem_id())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));

        if(!Objects.isNull(auction_Items ) && auction_Items.getImages() != null) {
            for (String image : auction_Items.getImages()) {
                try{
                    String publicId = ectractPublucId(image);
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<MultipartFile> images = request.getImages();
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = image.getOriginalFilename();
            if(fileName != null && !fileName.isEmpty()) {
                try{
                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(),ObjectUtils.emptyMap());
                    String fileUrl = uploadResult.get("url").toString();

                    fileNames.add(fileUrl);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }

        User user = new User();
        user.setId(request.getUserId());

        if(!Objects.isNull(auction_Items)) {
            auction_Items.setItem_name(request.getItem_name());
            auction_Items.setDescription(request.getDescription());
            auction_Items.setImages(fileNames);
            auction_Items.setStarting_price(request.getStarting_price());
            auction_Items.setStart_date(request.getStart_date());
            auction_Items.setEnd_date(request.getEnd_date());
            auction_Items.setBid_step(request.getBid_step());
            auction_Items.setUser(user);

        }

        auction_ItemsRepository.save(auction_Items);
    }

    public void deleteAuction_Items (Integer item_id){
        Auction_Items auction_Items = auction_ItemsRepository.findById(item_id).orElse(null);
        if(!Objects.isNull(auction_Items)) {
            List<String> images = auction_Items.getImages();

            for(String image : images) {
                try{
                    String publicId = ectractPublucId(image);
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        auction_ItemsRepository.deleteById(item_id);
    }

    private String ectractPublucId(String image) {
        if(image == null && image.isEmpty()) {
            throw new IllegalArgumentException("Invalid image URL");
        }

        String[] parts = image.split("/");
        String fileName = parts[parts.length - 1];

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public boolean updateStatus(int id) {
        Auction_Items auction_Items = auction_ItemsRepository.findById(id).orElse(null);
        if(!Objects.isNull(auction_Items)) {
            boolean updateStatus = !auction_Items.isStatus();
            auction_Items.setStatus(updateStatus);
            auction_ItemsRepository.save(auction_Items);

            if(auction_Items.isStatus()) {
                NotificationAuctionItem notificationAuctionItem = new NotificationAuctionItem();
                notificationAuctionItem.setAuctionItemId(auction_Items.getItem_id());
                notificationAuctionItem.setCreatedAt(new Date());
                notificationAuctionItem.setUpdatedAt(new Date());
                notificationAuctionItem.setType("T");
                notificationAuctionItem.setCreator(auction_Items.getUser());

                notificationAuctionItemRepository.save(notificationAuctionItem);

                NotificationAuctionItemResponse notificationAuctionItemResponse = NotificationAuctionItemMapper
                        .toNotificationAuctionItemResponse(notificationAuctionItem);
                messagingTemplate.convertAndSend("/topic/notification/product/status", notificationAuctionItemResponse);
            }


            return true;
        }

        return false;
    }


    public List<Auction_ItemsResponse> getAuctionsOnHome(){
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updatedAt")));
        Page<Auction_Items> auctionItemsPage = auction_ItemsRepository.findAll(pageable);
        return auctionItemsPage.stream()
                .map(Auction_ItemsMapper::toAuction_ItemsResponse)
                .collect(Collectors.toList());
    }



    //truong
    public List<Auction_Items> findUpcomingAuctionsByCreator(String userId) {
        return auction_ItemsRepository.findByUserIdAndIsSellFalse(userId);
    }
    public List<Auction_Items> findActiveAuctionsByCreator(String userId) {
        return auction_ItemsRepository.findByUserIdAndIsSellTrue(userId);
    }
}
