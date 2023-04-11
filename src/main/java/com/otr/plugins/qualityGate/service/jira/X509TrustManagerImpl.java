package com.otr.plugins.qualityGate.service.jira;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class X509TrustManagerImpl implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
