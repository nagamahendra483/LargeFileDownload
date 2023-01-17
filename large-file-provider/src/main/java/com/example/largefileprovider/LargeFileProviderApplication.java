package com.example.largefileprovider;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
@RestController
public class LargeFileProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(LargeFileProviderApplication.class, args);
	}

	@GetMapping(value = "/getLargeFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void getLargeFile(@RequestParam String path, HttpServletResponse response) {
		File file = new File("C:\\Users\\kowsh\\Projects\\Files\\" + path);
		try (FileInputStream fis = new FileInputStream(file)){
			IOUtils.copyLarge(fis, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
