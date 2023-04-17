package com.app.springapp2.requests;

import com.app.springapp2.utilities.JsonSerializer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.runner.Request;

import java.util.List;
import java.util.Map;

public class BaseRequests {

    private RequestSpecification request;
    private Response response;

    BaseRequests (String baseURI) {
        RestAssured.baseURI = baseURI;
    }
    public void prepareUnauth (JSONObject body) {
        request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(body.toJSONString());
    }

    public void prepareWithAuth (JSONObject body, String token) {
        request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer " + token);
        request.body(body.toJSONString());
    }

    public void send (String tail) {
        response = request.post(tail);
    }

    public void execGet (String token, String tail) {
        request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer " + token);
        response = request.get(tail);
    }
    public Response getResponse() {return response;}

    public Map<String,Object> getRespondedParameters() {
        String body = response.getBody().asString();
        return JsonSerializer.gson().fromJson(body, Map.class);
    }

    public List<Map<String,Object>> getRespondedListOfParameters() {
        String body = response.getBody().asString();
        List<Map<String,Object>> list = JsonSerializer.gson().fromJson(body, List.class);
        return list;
    }
}
