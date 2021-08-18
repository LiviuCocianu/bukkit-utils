package io.github.idoomful.bukkitutils.statics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;

public class JsonUtils {
    public static String toJson(Object obj) {
        return new Gson().toJson(obj);
    }

    public static <T> T fromJson(String input, Class<T> objClass) {
        return new Gson().fromJson(input, objClass);
    }

    public static String toJson(Collection<Object> obj) {
        return new Gson().toJson(obj);
    }

    public static <T> Collection<T> fromJSON(String json, Class<T> elemClass) {
        Gson gson = new Gson();
        ArrayList<T> output = new ArrayList<>();
        ArrayList<String> jsonList = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
        jsonList.forEach(pos -> output.add(gson.fromJson(pos, elemClass)));

        return output;
    }
}
