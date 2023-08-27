package io.github.idoomful.bukkitutils.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;

public class JsonCollection<T> implements JSONable {
    private Collection<T> list;

    public JsonCollection(Collection<T> list) {
        this.list = list;
    }

    public Collection<T> getList() {
        return list;
    }

    public void setList(Collection<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        out.append("[");

        int i = 0;
        for(T el : list) {
            out.append(el.toString());
            if(i < list.size() - 1) out.append(", ");
            i++;
        }

        out.append("]");
        return out.toString();
    }

    public static <E> JsonCollection<E> fromJSON(String json) {
        return new Gson().fromJson(json, new TypeToken<JsonCollection<E>>(){}.getType());
    }

    public static <E> String toJsonList(Collection<JsonCollection<E>> input) {
        ArrayList<String> output = new ArrayList<>();
        input.forEach(obj -> output.add(obj.toJSON()));
        return new Gson().toJson(output);
    }

    public static <E> ArrayList<JsonCollection<E>> fromJSONList(String json) {
        Gson gson = new Gson();
        ArrayList<JsonCollection<E>> output = new ArrayList<>();
        ArrayList<String> jsonList = gson.fromJson(json, new TypeToken<Collection<String>>(){}.getType());
        jsonList.forEach(pos -> output.add(gson.fromJson(pos, new TypeToken<JsonCollection<E>>(){}.getType())));

        return output;
    }
}
