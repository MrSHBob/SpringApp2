package com.app.springapp2.requests;

import net.minidev.json.JSONObject;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PositionRequests extends BaseRequests{

    private String token;

    public PositionRequests(String baseURI, String token) {
        super(baseURI);
        this.token = token;
    }

    public Long createPosition(
            Long product,
            Double mass,
            String date
    ) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("product", product);
        requestParams.put("mass", mass);
        requestParams.put("date", date);

        prepareWithAuth(requestParams, token);
        send("api/position/add");

        String str = getRespondedParameters().get("id").toString();
        return Long.parseLong(str.substring(0, str.length()-2));
    }

    public Long updatePosition(
            Long id,
            Long product,
            Double mass,
            String date
    ) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("id", id);
        requestParams.put("product", product);
        requestParams.put("mass", mass);
        requestParams.put("date", date);

        prepareWithAuth(requestParams, token);
        send("api/position/update");

        String str = getRespondedParameters().get("id").toString();
        return Long.parseLong(str.substring(0, str.length()-2));
    }

    public void deletePosition(
            Long id
    ) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("id", id);

        prepareWithAuth(requestParams, token);
        send("api/position/delete");
    }

    public void requestDailyPositions(
            String date
    ) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("date", date);

        prepareWithAuth(requestParams, token);
        send("api/position/dailylist");
    }

}
