package in.lms.sinchan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CurrencyConversion {

	public static Map<String, JSONObject> countryDetails = new TreeMap<String, JSONObject>();

	/*
	 * https://www.amdoren.com/developer/home/
	 * https://www.amdoren.com/api/currency.php?api_key=
	 * KyV4wEsRjQJdnTgSfmFT8dLuYpaWJP&from=EUR&to=GBP&amount=50
	 */
	public Double currencyConverter(Double amount, String curFrom, String curTo) {
		StringBuffer buffer;
		String finalData;
		try {
			URL url = new URL("https://www.amdoren.com/api/currency.php?api_key=KyV4wEsRjQJdnTgSfmFT8dLuYpaWJP&from="
					+ curFrom + "&to=" + curTo + "&amount=" + amount);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			if (urlConnection.getResponseCode() != 200) {
				log.info(":::::Failed to connect to the URL");
			}
			urlConnection.setRequestMethod("GET");
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			buffer = new StringBuffer();
			String input = "";
			while ((input = bufferReader.readLine()) != null) {
				buffer.append(input + "\n");
			}
			finalData = buffer.toString();
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(finalData);
			return (Double) jsonObject.get("amount");
		} catch (MalformedURLException e) {

		} catch (IOException e) {
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * https://currencyfreaks.com/
	 */
	public Double getUSBBasedCurrencyRate(String countryCode) {
		StringBuffer buffer;
		String finalData;
		Double dollerRate = null;
		try {
			URL url = new URL("https://api.currencyfreaks.com/latest?apikey=ec57f1acb5334a928e342fe8cc00a17b");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			if (urlConnection.getResponseCode() != 200) {
				log.info("::::::Failed to connect to the given url::::::");
				return null;
			}
			urlConnection.setReadTimeout(10000);
			urlConnection.setRequestMethod("GET");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			buffer = new StringBuffer();
			String input = "";
			while ((input = bufferedReader.readLine()) != null) {
				buffer.append(input + "\n");
			}
			finalData = buffer.toString();
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(finalData);
			JSONObject rates = (JSONObject) jsonObject.get("rates");
			dollerRate = Double.valueOf((String) rates.get(countryCode));
			loadCurrencyDetails();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dollerRate;
	}

	/*
	 * https://free.currconv.com/api/v7/countries?apiKey=a890a643b407a78a0912
	 */
	@Scheduled(fixedRate = 600000)
	public void loadCurrencyDetails() {
		StringBuffer buffer;
		String finalData;
		try {
			URL url = new URL("https://restcountries.eu/rest/v2/all");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			if (urlConnection.getResponseCode() != 200) {
				log.info("::::::Failed to connect to the given url::::::");
				return;
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String input;
			buffer = new StringBuffer();
			while ((input = bufferedReader.readLine()) != null) {
				buffer.append(input + "\n");
			}
			finalData = buffer.toString();
			JSONArray jsonArray = (JSONArray) new JSONParser().parse(finalData);
			for (int iter = 0; iter < jsonArray.size(); iter++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(iter);
				log.info("Country Name : {}", jsonObject.get("name"));
				countryDetails.put((String) jsonObject.get("alpha2Code"), jsonObject);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
