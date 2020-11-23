package in.lms.sinchan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.lms.sinchan.util.CurrencyConversion;

@RestController
@RequestMapping(value = "/v1/currency")
public class PaymentController {

	@Autowired
	private CurrencyConversion currencyConversion;

	@GetMapping(path = "/{countryCode}")
	public ResponseEntity<ModelMap> getDollerRate(
			@PathVariable(value = "countryCode", required = true) String countryCode) {
		Double rate = currencyConversion.getUSBBasedCurrencyRate(countryCode);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ModelMap().addAttribute("Country Code", countryCode).addAttribute("rate", rate));
	}

	@GetMapping(value = "/convert/{amount}")
	public ResponseEntity<ModelMap> convertCurrency(@PathVariable(value = "amount", required = true) Double amount,
			@RequestParam(value = "curFrom", required = true) String curFrom,
			@RequestParam(value = "curTo", required = true) String curTo) {
		Double convertedAmount = currencyConversion.currencyConverter(amount, curFrom, curTo);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ModelMap().addAttribute("Amount", amount).addAttribute("From", curFrom)
						.addAttribute("To", curTo).addAttribute("Converted Amount", convertedAmount));
	}

}
