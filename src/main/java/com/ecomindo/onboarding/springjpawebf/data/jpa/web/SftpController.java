package com.ecomindo.onboarding.springjpawebf.data.jpa.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.ecomindo.onboarding.springjpawebf.data.jpa.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.data.jpa.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/sftp")
public class SftpController {

	@Autowired
	FileService fileService;

//	@ApiParam(allowMultiple=true) 
	@RequestMapping(path = "/upload", method = RequestMethod.POST, 
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<String> upload(@RequestPart(name = "file") Flux<FilePart> parts) {
			return parts
            .filter(part -> part instanceof FilePart) // only retain file parts
            .ofType(FilePart.class) // convert the flux to MultipartFile
            .flatMap(this::upload); // save each file and flatmap it to a flux of results
	}
	
	private Mono<String> upload(FilePart file){
		try {
			return fileService.upload(file);
		} catch (Exception e) {
			ResponseDTO response = new ResponseDTO();
			response.setCode("500");
			response.setMessage("Upload Failed");
			e.printStackTrace();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			try {
				String json = ow.writeValueAsString(response);
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return Mono.just("{\"code\":\"500\", \"message\":\"Unhandled error occured!\"}");
		}
	}

}
