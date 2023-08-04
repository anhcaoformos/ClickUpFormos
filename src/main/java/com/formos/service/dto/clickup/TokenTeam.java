package com.formos.service.dto.clickup;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import org.apache.http.Header;

public class TokenTeam {

    private Header token;
    private String originTeam;
    private Team team;

    public TokenTeam(Header token, JsonObject originTeam) {
        this.token = token;
        Gson gson = new Gson();
        this.originTeam = gson.toJson(originTeam);
        this.team = gson.fromJson(originTeam, Team.class);
    }

    public Header getToken() {
        return token;
    }

    public void setToken(Header token) {
        this.token = token;
    }

    public String getOriginTeam() {
        return originTeam;
    }

    public void setOriginTeam(String originTeam) {
        this.originTeam = originTeam;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
