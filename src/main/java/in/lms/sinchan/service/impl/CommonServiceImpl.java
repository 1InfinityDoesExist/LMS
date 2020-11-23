package in.lms.sinchan.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import in.lms.sinchan.service.CommonService;
import in.lms.sinchan.util.CurrencyConversion;

@Component
public class CommonServiceImpl implements CommonService {

	@Override
	public ModelMap getInternetDetails() {
		ModelMap modelMap = new ModelMap();
		StringBuffer buffer;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			modelMap.addAttribute("SystemIP", inetAddress.getHostAddress().trim());
			URL url = new URL("http://bot.whatismyipaddress.com/");
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String input = "";
			buffer = new StringBuffer();
			while ((input = bufferedReader.readLine()) != null) {
				buffer.append(input);
			}
			String publicIP = buffer.toString();
			modelMap.addAttribute("PublicIP", publicIP);
			String countryCode = getContryCode(publicIP);
			modelMap.addAttribute("Details", CurrencyConversion.countryDetails.get(countryCode));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelMap;
	}

	private String getContryCode(String publicIP) throws MalformedURLException {
		StringBuffer buffer;
		String finalData = "";
		try {
			URL url = new URL("https://freegeoip.app/json/" + publicIP);
			URLConnection urlConnectioni = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnectioni.getInputStream()));
			String input = "";
			buffer = new StringBuffer();
			while ((input = bufferedReader.readLine()) != null) {
				buffer.append(input + "\n");
			}
			finalData = buffer.toString();
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(finalData);
			return (String) jsonObject.get("country_code");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
