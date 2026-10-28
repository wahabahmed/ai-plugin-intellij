package com.turbo.dev.plugin.client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turbo.dev.plugin.dto.ScanResult;
import com.turbo.dev.plugin.dto.chat.ChatRequest;
import com.turbo.dev.plugin.dto.chat.ChatResponse;
import com.turbo.dev.plugin.dto.scan.ScanRequest;
import com.turbo.dev.plugin.dto.subscription.SubscriptionPayment;
import com.turbo.dev.plugin.exception.BackendErrorDto;
import com.turbo.dev.plugin.utils.TokenStorage;
import org.apache.http.HttpStatus;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

public class ApiClient {
    private static final String baseUrl = "https://localhost:9991";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Object /*ScanResult*/ sendScanToBackend(ScanRequest jsonBody) {
        String token = TokenStorage.getAccessToken();
        try {
            String body = mapper.writeValueAsString(jsonBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/ai/scan"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    createClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.SC_OK) {
                String json = response.body();
                if (json != null && json.length() > 1) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(json, ScanResult.class);
                } else {
                    throw new Exception("response is empty");
                }
            } else if (response.statusCode() == HttpStatus.SC_PAYMENT_REQUIRED) {
                return preparePaymentPlanFromResponse(response);
            }
        } catch (Exception e) {
            throw new RuntimeException("API call failed" + e.getMessage());
        }

        return null;
    }

    public static List<?> sendArchitectureToBackend(ScanRequest jsonBody) {
        String token = TokenStorage.getAccessToken();
        try {
            String body = mapper.writeValueAsString(jsonBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/ai/architecture"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    createClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.SC_OK) {
                String json = response.body();
                if (json != null && json.length() > 1) {
                    ObjectMapper mapper = new ObjectMapper();
                    return Arrays.asList(mapper.readValue(json, String[].class));
                } else {
                    throw new Exception("response is empty");
                }
            } else if (response.statusCode() == HttpStatus.SC_PAYMENT_REQUIRED) {
                return preparePaymentPlanFromResponse(response);
            }
        } catch (Exception e) {
            throw new RuntimeException("API call failed" + e.getMessage());
        }

        return null;
    }

    public static Object processAiPrompt(ChatRequest jsonBody) {
        String token = TokenStorage.getAccessToken();
        try {
            String body = mapper.writeValueAsString(jsonBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/ai/chat"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    createClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.SC_OK) {
                String json = response.body();
                if (json != null && json.length() > 1) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(json, ChatResponse.class);
                } else {
                    throw new Exception("response is empty");
                }
            } else if (response.statusCode() == HttpStatus.SC_PAYMENT_REQUIRED) {
                return preparePaymentPlanFromResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API call failed" + e.getMessage());
        }

        return null;
    }

    public static Object checkUser() {
        String token = TokenStorage.getAccessToken();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/ai/user"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response =
                    createClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.SC_OK) {
                // user checked
            } else if (response.statusCode() == HttpStatus.SC_PAYMENT_REQUIRED) {
                return preparePaymentPlanFromResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API call failed" + e.getMessage());
        }

        return null;
    }

    /*public static List<SubscriptionPayment> getPlans() {
        String token = TokenStorage.getAccessToken();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/ai/subscriptions/payment-options"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response =
                    createClient().send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();
            if (json != null && json.length() > 1) {
                ObjectMapper mapper = new ObjectMapper();
                return Arrays.asList(mapper.readValue(json, SubscriptionPayment[].class));
            } else {
                throw new Exception("response is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API call failed" + e.getMessage());
        }
    }*/

    private static List<SubscriptionPayment> preparePaymentPlanFromResponse(HttpResponse<String> response) throws JsonProcessingException {
        String json = response.body();
        ObjectMapper mapper = new ObjectMapper();
        BackendErrorDto paymentPlans = mapper.readValue(json, BackendErrorDto.class);
        return paymentPlans.getPaymentOptions();
    }

    public static HttpClient createClient() {
        try {

            X509ExtendedTrustManager trustAll = new X509ExtendedTrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, java.net.Socket socket) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, java.net.Socket socket) {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustAll}, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
