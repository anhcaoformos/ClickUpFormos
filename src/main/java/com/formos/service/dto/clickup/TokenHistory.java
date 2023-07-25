package com.formos.service.dto.clickup;

import org.apache.http.Header;

public class TokenHistory {

    private Header token;
    private HistoryData historyData;

    public TokenHistory(Header token, HistoryData historyData) {
        this.token = token;
        this.historyData = historyData;
    }

    public Header getToken() {
        return token;
    }

    public void setToken(Header token) {
        this.token = token;
    }

    public HistoryData getHistoryData() {
        return historyData;
    }

    public void setHistoryData(HistoryData historyData) {
        this.historyData = historyData;
    }
}
