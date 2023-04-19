package com.otr.plugins.qualityGate.model;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.ICredentials;

import java.io.Serializable;

public record JiraSettings(String login, String password, String url) implements Serializable {

    public ICredentials createCredentials() {
        return new BasicCredentials(login, password);
    }
}
