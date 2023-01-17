package com.example.largefilemediator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@RestController
public class LargeFileMediatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LargeFileMediatorApplication.class, args);
	}

	public Flux<DataBuffer> downloadFileUrl(String fileName) throws IOException {

		WebClient webClient = WebClient.create("http://localhost:8080");

		// Request service to get file data
		return webClient.get()
				.uri( uriBuilder ->
						uriBuilder
								.path("/getLargeFile")
								.queryParam("path", fileName)
								.build() )
				.accept( MediaType.APPLICATION_OCTET_STREAM )
				.retrieve()
				.bodyToFlux( DataBuffer.class );
	}

	@GetMapping(value = "/getLargeFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void downloadFile(@RequestParam String fileName, HttpServletResponse response ) throws IOException
	{
		Flux<DataBuffer> dataStream = this.downloadFileUrl(fileName);

		// Streams the stream from response instead of loading it all in memory
		DataBufferUtils.write( dataStream, response.getOutputStream() )
				.map( DataBufferUtils::release )
				.blockLast();
	}

}
