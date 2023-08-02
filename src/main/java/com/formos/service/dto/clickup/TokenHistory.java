package com.formos.service.dto.clickup;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.util.List;
import org.apache.http.Header;

public class TokenHistory {

    private Header token;
    private String originHistories;
    private List<History> histories;

    public TokenHistory(Header token, JsonArray originHistories) {
        this.token = token;
        Gson gson = new Gson();
        this.originHistories = gson.toJson(originHistories);
        this.histories = gson.fromJson(originHistories, new TypeToken<List<History>>() {}.getType());
    }

    public Header getToken() {
        return token;
    }

    public void setToken(Header token) {
        this.token = token;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public String getOriginHistories() {
        return originHistories;
    }

    public void setOriginHistories(String originHistories) {
        this.originHistories = originHistories;
    }
}
