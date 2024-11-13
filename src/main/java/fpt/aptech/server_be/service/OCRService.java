package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.repositories.UserRepository;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRService {
    private final Tesseract tesseract;
    private final UserService userService;
    private final UserRepository userRepository;

    public OCRService(Tesseract tesseract, UserService userService, UserRepository userRepository) {
        this.tesseract = tesseract;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public static final String BASEURL = "C:\\Users\\khuye\\IdeaProjects\\server_be\\src\\main\\resources\\static\\images";
//String
    public void getImageString(String userId,MultipartFile multipartFile) throws IOException, TesseractException {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        final String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        Path filePath = Path.of(BASEURL, originalFileName);

        Path directoryPath = Path.of(BASEURL);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try {
            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved at: " + filePath.toString());

            String ocrText = tesseract.doOCR(filePath.toFile());
            System.out.println("OCR Result: " + ocrText);

//            String extractedInfo = extractCardInfo(ocrText);
//            extractCardInfo(ocrText);

            String name = extractName(ocrText);
            String idNumber = extractIdNumber(ocrText);
//            String birthDate = extractBirthDate(ocrText);
            String address = extractAddress(ocrText);

            user.setName(name);
//            user.setDob(LocalDate.parse(birthDate));
            user.setAddress(address);
            user.setCiNumber(idNumber);

            userRepository.save(user);

            Files.delete(filePath);

//            return extractedInfo;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error saving file", e);
        }
    }
//String
//    private void extractCardInfo(String ocrText) {
//        String name = extractName(ocrText);
//        String idNumber = extractIdNumber(ocrText);
//        String birthDate = extractBirthDate(ocrText);
//        String address = extractAddress(ocrText);
//
//        User user = new User();
//        user.setName(name);
//        user.setDob(LocalDate.parse(birthDate));
//        user.setAddress(address);
//        user.setCiNumber(idNumber);
//
//        userService.updateUser(user);
//
//
////        return "Name: " + name + "\nID: " + idNumber + "\nDOB: " + birthDate + "\nAddress: " + address;
//    }

    private String extractName(String ocrText) {
        ocrText = ocrText.replaceAll("[^a-zA-ZÀ-ỹ ]", "");

        Pattern namePattern = Pattern.compile("Họ và tên (.*?)Ngày");
        Matcher matcher = namePattern.matcher(ocrText);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }


    private String extractIdNumber(String ocrText) {
        Pattern idPattern = Pattern.compile("(\\d{12})");
        Matcher matcher = idPattern.matcher(ocrText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

//    private String extractBirthDate(String ocrText) {
//        Pattern birthDatePattern = Pattern.compile("Ngày. tháng, năm sinh (\\d{2}/\\d{2}/\\d{4})");
//        Matcher matcher = birthDatePattern.matcher(ocrText);
//        if (matcher.find()) {
//            return matcher.group(1);
//        }
//        return "1999-1-1";
//    }

    private String extractAddress(String ocrText) {
        ocrText = ocrText.replaceAll("[^a-zA-ZÀ-ỹ, ]", "");

        System.out.println("Cleaned OCR Text: " + ocrText);

        Pattern addressPattern = Pattern.compile("Quê quá[:：]?\\s*(.*?)(- Nơi thường|Có giá trị đến|$)");
        Matcher matcher = addressPattern.matcher(ocrText);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
}
