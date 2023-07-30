package com.formos.service.dto.clickup;

import java.util.List;
import org.apache.http.Header;

public class TokenHistory {

    private Header token;
    private List<History> histories;

    public TokenHistory(Header token, List<History> histories) {
        this.token = token;
        this.histories = histories;
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
}
