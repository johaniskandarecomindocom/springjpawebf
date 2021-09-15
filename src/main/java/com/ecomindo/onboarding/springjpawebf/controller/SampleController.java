package com.ecomindo.onboarding.springjpawebf.controller;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecomindo.onboarding.springjpawebf.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.model.City;
import com.ecomindo.onboarding.springjpawebf.service.CityService;

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
	@ResponseBody
	@Transactional()
	public ResponseDTO insert(@RequestBody City city) {
		ResponseDTO response = new ResponseDTO();
		try {
			City saved = cityService.insert(city.getName(), city.getCountry(), 
					city.getState(), city.getMap());
			
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
	@PostMapping("/insert-future")
	@ResponseBody
	@Transactional()
	public ResponseDTO insertUsingFuture(@RequestBody City city) {
		ResponseDTO response = new ResponseDTO();
		try {
			Future<City> future = cityService.insertLongTime(city.getName(), 
					city.getCountry(), city.getState(), city.getMap());

			while (!future.isDone()) {
				System.out.println("Waiting for database process...");
				Thread.sleep(300);
			}

			City futureCity = future.get();

			response.setCode("200");
			response.setMessage("Insert Success");
			response.setData(futureCity);

			return response;
		} catch (Exception e) {
			response.setCode("500");
			response.setMessage("Insert Failed");
			return response;
		}
	}

	@PostMapping("/insert-future2")
	@ResponseBody
	@Transactional()
	public ResponseDTO insertUsingFuture2(@RequestBody City city) {
		ResponseDTO response = new ResponseDTO();
		try {
			Future<City> future1 = cityService.insertLongTime(city.getName() + "1", 
					city.getCountry(), city.getState(), city.getMap());
			Future<City> future2 = cityService.insertLongTime(city.getName() + "2", 
					city.getCountry(), city.getState(), city.getMap());
			Future<City> future3 = cityService.insertLongTime(city.getName() + "3", 
					city.getCountry(), city.getState(), city.getMap());
			
			while (!(future1.isDone() && future2.isDone() && future3.isDone())) {
			    if(future1.isDone()) {
			    	System.out.println("future1 is done");
			    } else {
			    	System.out.println("future1 is not done");
			    }
			    if(future2.isDone()) {
			    	System.out.println("future2 is done");
			    }
			    else {
			    	System.out.println("future2 is not done");
			    }
			    if(future3.isDone()) {
			    	System.out.println("future3 is done");
			    }
			    else {
			    	System.out.println("future3 is not done");
			    }
			    Thread.sleep(300);
			}

			ArrayList<City> res = new ArrayList<>();
			res.add(future1.get());
			res.add(future2.get());
			res.add(future3.get());
			
			response.setCode("200");
			response.setMessage("Insert Success");
			response.setData(res);
			
			return response;
		} catch (Exception e) {
			response.setCode("500");
			response.setMessage("Insert Failed");
			return response;
		}
	}
}
