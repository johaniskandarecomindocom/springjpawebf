package com.ecomindo.onboarding.springjpawebf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecomindo.onboarding.springjpawebf.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.redis.dao.UserRepository;
import com.ecomindo.onboarding.springjpawebf.redis.model.User;

@RestController
public class UserController {
	@Autowired
	private UserRepository _userRepository;

	@GetMapping("/all")
	public ResponseDTO GetAll() {
		ResponseDTO response = new ResponseDTO();

		response.setCode("200");
		response.setData(_userRepository.findAll());

		return response;
	}

	@GetMapping("/all/{id}")
	public ResponseDTO GetAll(@PathVariable("id") final String id) {
		ResponseDTO response = new ResponseDTO();

		response.setCode("200");
		response.setData(_userRepository.findById(id));

		return response;

	}

	@PostMapping("/add")
	public ResponseDTO add(@RequestBody User user) {
		ResponseDTO response = new ResponseDTO();

		_userRepository.save(new User(user.getId(), user.getName(), user.getAddress()));
		
		response.setCode("200");
		response.setData(_userRepository.findById(user.getId()));

		return response;
	}

	@PostMapping("/update")
	public ResponseDTO update(@RequestBody User user) {
		ResponseDTO response = new ResponseDTO();

		_userRepository.save(new User(user.getId(), user.getName(), user.getAddress()));

		response.setCode("200");
		response.setData(_userRepository.findById(user.getId()));

		return response;

	}

	@PostMapping("/delete")
	public ResponseDTO delete(@PathVariable("id") final String id) {
		ResponseDTO response = new ResponseDTO();

		_userRepository.deleteById(id);

		response.setCode("200");
		response.setMessage("Deleted");

		return response;

	}
}
