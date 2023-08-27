package io.github.idoomful.bukkitutils.json;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;

public interface JSONable {
    @NotNull
    default String toJSON() {
        return new Gson().toJson(this);
    }
    @CheckForNull
    @Nullable
    static JSONable fromJSON(@NotNull String json, Class<? extends JSONable> clazz) {
        return new Gson().fromJson(json, clazz);
    }
}
