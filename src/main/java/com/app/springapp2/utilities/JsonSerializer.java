package com.app.springapp2.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JsonSerializer {

    private static Gson g = null;
    public static Gson gson() {
        if (g == null) g = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateJsonSerializeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonSerializeAdapter())
                .create();
        return g;
    }
}
