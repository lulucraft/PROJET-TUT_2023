package fr.nepta.cloud.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import fr.nepta.cloud.model.Offer;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class PayPalService {

	private static OfferService os;

	@Autowired
	private OfferService offerService;

	@PostConstruct
	public void init() {
		os = offerService;
	}

	public static Offer getOrderOffer(String orderId) throws IOException, InterruptedException, JSONException {
		JSONObject checkoutOrders = getCheckoutOrders(orderId);

		if (checkoutOrders == null) return null;

		//		System.out.println(checkoutOrders);
		//		System.out.println(checkoutOrders.toString());
		//		System.out.println(checkoutOrders.length());

		//		ObjectMapper om = new ObjectMapper();
		//		//om.activateDefaultTyping(new DefaultBaseTypeLimitingValidator());
		//		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//		System.err.println(om.readValue(checkoutOrders.toString(), PurshaseUnits.class).items);

		// Get purshase_units of order
		JSONArray pu = (JSONArray) checkoutOrders.get("purchase_units");
		if (pu == null) {
			log.error("Impossible to get purchase_units of order '{}'", orderId);
			return null;
		}

		// Get purshase_units items
		JSONArray items = (JSONArray) ((JSONObject) pu.get(0)).get("items");
		if (items == null) {
			log.error("Impossible to get items of purshase_units of order '{}'", orderId);
			return null;
		}

		// Get purshase_units items and convert to product
		for (int i = 0; i < items.length(); i++) {
			JSONObject jsonProduct = (JSONObject) items.get(i);

			if (jsonProduct == null) {
				log.error("Impossible to get item '{}' of purshase_units items of order '{}'", i, orderId);
				continue;
			}

			Offer o = os.getOffer((String) jsonProduct.get("name"));
			if (o != null) {
				System.out.println(o.getName());
				return o;
//				int quantity = (int) Integer.parseInt((String) jsonProduct.get("quantity"));
//				System.err.println(quantity);
//				for (int i1 = 0; i1 < quantity; i1++) {
//					products.add(p);
//				}
			}
		}

		return null;
	}

	private static String getToken() throws IOException, InterruptedException {
		// POST request to get token
		String urlStr = "https://api.sandbox.paypal.com/v1/oauth2/token";
		String data = "grant_type=client_credentials";

		HttpRequest request = HttpRequest.newBuilder(URI.create(urlStr))
				.setHeader("Accept", "application/json")
				.setHeader("Authorization", "Basic " + "QWFEX2VBckwzbEltU3NVbTZFUHFDMVhQaFM2VFoxd2tOdDdERWFtTzhsVVVKdzl4UTFnZi1fcXZXNGlBZUZ1M1Zac0pSNjEtTk41UW8xQUY6RUZBY0VwaElZQkVkTjRKVzZzUHFnQVpnX3p5UElEejhpOXh4WDlzamRGSGhrVVlMQ21RQnBLWVJWZWR3X2t0OGpjaXhfMFRRSVVCLWdvZzI")
				.setHeader("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(data))
				.build();

		HttpClient client = HttpClient.newBuilder().build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

		try {
			JSONObject jsonResponse = new JSONObject(new JSONParser(response.body()).object());
//			JSONObject jsonResponse = (JSONObject) response.body();
//			jsonResponse = (JSONObject) new JSONParser(response.body().toString()).parse();
			return jsonResponse.getString("access_token");
		} catch (JSONException | ParseException e) {
			log.error("PayPal access_token request failed");
			e.printStackTrace();
		}

		return null;
	}

	private static JSONObject getCheckoutOrders(String orderId) throws IOException, InterruptedException {
		// GET PayPal access_token
		String access_token = getToken();

		if (access_token == null) {
			return null;
			//throw new AuthenticationException("Erreur lors de la recuperation de l'access_token PayPal");
		}

		String urlStr = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId;

		// Use the token to get payments authorizations
		HttpRequest request = HttpRequest.newBuilder(URI.create(urlStr))
				.setHeader("Authorization", "Bearer " + access_token)
				.setHeader("Content-Type", "application/json")
				.GET()
				.build();

		HttpClient client = HttpClient.newBuilder().build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

		JSONObject jsonResponse = null;
		try {
			jsonResponse = new JSONObject(new JSONParser(response.body()).object());
		} catch (ParseException e) {
			log.error("Authorizations request failed for order '{}'", orderId);
			e.printStackTrace();
		}

		return jsonResponse;
	}

}