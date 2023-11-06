package com.huy.appnoithat.Service.LuaChonNoiThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.functions.T;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CacheNoiThatRequestService {
    HashMap<String, String> cache = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    public String createUniqueId(String... param) {
        String seed = "";
        for (String s : param) {
            seed = seed.concat(s);
        }
        UUID uuid = UUID.nameUUIDFromBytes(seed.getBytes());
        return uuid.toString();
    }
    public <X> void writeCache(List<X> data, String uniqueId) {
        try {
            String result = objectMapper.writeValueAsString(data);
            cache.put(uniqueId, result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public <C> List<C> readCache(String uniqueId, Class<C> objectType) {
        String result = cache.get(uniqueId);
        if (result == null) {
            return null;
        }
        try {
            return objectMapper.readValue(result,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, objectType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isContain(String uniqueId) {
        return cache.containsKey(uniqueId);
    }
}
