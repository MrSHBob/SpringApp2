package com.app.springapp2.requests;

import com.app.springapp2.model.User;
import net.minidev.json.JSONObject;

public class UserRequests extends BaseRequests{

    public UserRequests(String baseURI) {
        super(baseURI);
    }

    public Long createUser(String username) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", username);
        requestParams.put("password", username);

        prepareUnauth(requestParams);
        send("api/auth/registration");

        String str = getRespondedParameters().get("id").toString();
        return Long.parseLong(str.substring(0, str.length()-2));
    }

    public String logon(String username) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", username);
        requestParams.put("password", username);

        prepareUnauth(requestParams);
        send("api/auth/authenticate");

        return getResponse().getBody().asString();
    }
}
