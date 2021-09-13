package com.ecomindo.onboarding.springjpawebf.data.jpa.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.City;
import com.ecomindo.onboarding.springjpawebf.data.jpa.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.data.jpa.service.CityService;

@Controller
public class SampleController {

	@Autowired
	private CityService cityService;

	@RequestMapping("/")
	@ResponseBody
	@Transactional(readOnly = true)
	public ResponseDTO helloWorld() {
		ResponseDTO response = new ResponseDTO();
		try {
			City saved = this.cityService.getCity("Bath", "UK");
			
			response.setCode("200");
			response.setMessage("Insert Success");
			response.setData(saved==null?"Not found":saved.getName());
			
			return response;
		} catch (Exception e) {
			response.setCode("500");
			response.setMessage("Insert Failed");
			return response;
		}
	}
	
	@PostMapping("/insert")
	public ResponseDTO insert(@RequestBody City city) {
		ResponseDTO response = new ResponseDTO();
		try {
			City saved = cityService.insert(city.getName(), city.getCountry(), city.getState());
			
			response.setCode("200");
			response.setMessage("Insert Success");
			response.setData(saved);
			
			return response;
		} catch (Exception e) {
			response.setCode("500");
			response.setMessage("Insert Failed");
			return response;
		}
	}
}
