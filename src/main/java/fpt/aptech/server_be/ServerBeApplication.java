package fpt.aptech.server_be;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ServerBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerBeApplication.class, args);
	}

	@Bean
	public Tesseract tesseract(){
		Tesseract tesseract = new Tesseract();
		tesseract.setLanguage("vie");
		tesseract.setPageSegMode(6);
		tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
		return tesseract;
	}
}
