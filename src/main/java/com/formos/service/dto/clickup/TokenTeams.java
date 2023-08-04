package com.formos.service.dto.clickup;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.util.List;
import org.apache.http.Header;

public class TokenTeams {

    private Header token;
    private String originTeams;
    private List<Team> teams;

    public TokenTeams(Header token, JsonArray originTeams) {
        this.token = token;
        Gson gson = new Gson();
        this.originTeams = gson.toJson(originTeams);
        this.teams = gson.fromJson(originTeams, new TypeToken<List<Team>>() {}.getType());
    }

    public Header getToken() {
        return token;
    }

    public void setToken(Header token) {
        this.token = token;
    }

    public String getOriginTeams() {
        return originTeams;
    }

    public void setOriginTeams(String originTeams) {
        this.originTeams = originTeams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
