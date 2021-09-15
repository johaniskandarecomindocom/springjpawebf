package com.ecomindo.onboarding.springjpawebf.service;

import org.springframework.http.codec.multipart.FilePart;

import com.ecomindo.onboarding.springjpawebf.dto.ResponseDTO;

import reactor.core.publisher.Mono;

public interface FileService {
	public Mono<ResponseDTO> upload(FilePart file) throws Exception;
}
