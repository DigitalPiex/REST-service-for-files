package com.example.filedemo.standartcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class Contr {

	@GetMapping("")
	public String test() {
		return "TestS";
	}

}
