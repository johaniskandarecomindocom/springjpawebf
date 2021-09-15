package com.ecomindo.onboarding.springjpawebf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.ecomindo.onboarding.springjpawebf.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.service.FileService;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/sftp")
public class SftpController {

	@Autowired
	FileService fileService;

//	@ApiParam(allowMultiple=true) 
	@PostMapping(path = "/upload", 
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseDTO> upload(@RequestPart(name = "file") Mono<FilePart> parts) {
			return parts
					.filter(part -> part instanceof FilePart)
	                .ofType(FilePart.class)
	                .log()
            .flatMap(this::upload); // save each file and flatmap it to a flux of results
	}
	
	private Mono<ResponseDTO> upload(FilePart file){
		try {
			return fileService.upload(file);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseDTO response = new ResponseDTO();
			response.setCode("500");
			response.setMessage("Upload Failed: " + e.getMessage());
			return Mono.just(response);
		}
	}

}
