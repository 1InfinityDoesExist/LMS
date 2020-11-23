package in.lms.sinchan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.lms.sinchan.service.CommonService;

@RestController("commonController")
@RequestMapping(value = "/v1/common")
public class CommonController {

	@Autowired
	private CommonService commonService;

	@GetMapping(value = "/get/internetDetails")
	public ResponseEntity<ModelMap> getInternatAddress() {

		ModelMap response = commonService.getInternetDetails();
		return ResponseEntity.status(HttpStatus.OK).body(new ModelMap().addAttribute("response", response));
	}

}
