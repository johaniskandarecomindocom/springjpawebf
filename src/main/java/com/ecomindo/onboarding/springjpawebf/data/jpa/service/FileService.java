package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import org.springframework.http.codec.multipart.FilePart;

import com.ecomindo.onboarding.springjpawebf.data.jpa.dto.ResponseDTO;

import reactor.core.publisher.Mono;

public interface FileService {
	public Mono<String> upload(FilePart file) throws Exception;
}
