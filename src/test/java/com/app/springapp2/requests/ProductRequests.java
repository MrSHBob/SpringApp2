package com.app.springapp2.requests;

import net.minidev.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ProductRequests extends BaseRequests{

    private String token;

    public ProductRequests(String baseURI, String token) {
        super(baseURI);
        this.token = token;
    }

    public Long createProduct(
            String product,
            Double price,
            Double b,
            Double j,
            Double u,
            Double kkl
    ) {
        JSONObject requestParams = new JSONObject();
//        requestParams.put("id", id);
        requestParams.put("name", product);
        requestParams.put("price", price);
        requestParams.put("b", b);
        requestParams.put("j", j);
        requestParams.put("u", u);
        requestParams.put("kkl", kkl);

        prepareWithAuth(requestParams, token);
        send("api/product/add");

        String str = getRespondedParameters().get("id").toString();
        return Long.parseLong(str.substring(0, str.length()-2));
    }

    public Long updateProduct(
            Long id,
            String product,
            Double price,
            Double b,
            Double j,
            Double u,
            Double kkl
    ) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("id", id);
        requestParams.put("name", product);
        requestParams.put("price", price);
        requestParams.put("b", b);
        requestParams.put("j", j);
        requestParams.put("u", u);
        requestParams.put("kkl", kkl);

        prepareWithAuth(requestParams, token);
        send("api/product/update");

        String str = getRespondedParameters().get("id").toString();
        return Long.parseLong(str.substring(0, str.length()-2));
    }


    public List<Map<String,Object>> getAllProducts() {
        execGet(token, "api/product/list");
        return getRespondedListOfParameters();
    }

}
