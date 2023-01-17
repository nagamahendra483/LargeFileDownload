package com.example.largefiledownload;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootApplication
@RestController
public class LargeFileDownloadApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(LargeFileDownloadApplication.class, args);
	}

	@GetMapping(value = "/test")
	public ResponseEntity<String> saveFile(@RequestParam String name) throws IOException {
		downloadFile(name);
		return ResponseEntity.ok("Done");
	}

	public Flux<DataBuffer> downloadFileUrl(String fileName) throws IOException {

		WebClient webClient = WebClient.create("http://localhost:8081");

		// Request service to get file data
		return webClient.get()
				.uri( uriBuilder ->
						uriBuilder
								.path("/getLargeFile")
								.queryParam("fileName", fileName)
								.build() )
				.accept( MediaType.APPLICATION_OCTET_STREAM )
				.retrieve()
				.bodyToFlux( DataBuffer.class );
	}

	public void downloadFile(String requestFileName) throws IOException
	{
		Flux<DataBuffer> dataStream = this.downloadFileUrl(requestFileName);

		try(FileOutputStream fos = new FileOutputStream(requestFileName)){
			DataBufferUtils.write( dataStream, fos )
					.map( DataBufferUtils::release )
					.blockLast();
		}

	}

}
