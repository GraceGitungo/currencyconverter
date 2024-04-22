package com;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class CurrencyConverter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter currency to convert from (e.g., USD):");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.println("Enter currency to convert to (e.g., EUR):");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.println("Enter amount to convert:");
        BigDecimal amount = scanner.nextBigDecimal();

        OkHttpClient client = new OkHttpClient();
        String apiUrl = "https://open.exchangerate-api.com/v6/latest/" + baseCurrency;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();

                Gson gson = new Gson();
                ExchangeRatesData ratesData = gson.fromJson(jsonResponse, ExchangeRatesData.class);

                if (ratesData != null && ratesData.rates.containsKey(targetCurrency)) {
                    BigDecimal exchangeRate = ratesData.rates.get(targetCurrency);
                    BigDecimal convertedAmount = amount.multiply(exchangeRate);

                    System.out.println(amount + " " + baseCurrency + " = " + convertedAmount + " " + targetCurrency);
                } else {
                    System.out.println("Invalid currency code or data unavailable for conversion.");
                }
            } else {
                System.out.println("Failed to fetch exchange rates. HTTP error code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Error fetching exchange rates: " + e.getMessage());
        }
    }

    static class ExchangeRatesData {
        String base;
        String disclaimer;
        long timestamp;
        java.util.Map<String, BigDecimal> rates;
    }
}
