package com.otr.plugins.qualityGate.service.jira;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Hacked jira-client
 */
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NonVerifyingJiraClient extends JiraClient {

    String uri;
    ICredentials credentials;
    @Getter
    boolean disable;

    public NonVerifyingJiraClient(String uri) {
        super(uri);
        this.uri = uri;
        this.credentials = null;
        this.disable = true;
    }

    public NonVerifyingJiraClient(String uri, ICredentials credentials) {
        super(uri, credentials);
        this.uri = uri;
        this.credentials = credentials;
        this.disable = false;
    }


    /**
     * Fake {@link RestClient} for {@link JiraClient} non verifying jira client
     * todo: use SSLConnectionSocketFactory instead of SSLSocketFactory
     */
    @PostConstruct
    public void hack() throws NoSuchAlgorithmException, KeyManagementException, NoSuchFieldException, IllegalAccessException {
        if (disable) {
            return;
        }

        SSLContext ctx = SSLContext.getInstance("SSL");

        ctx.init(null, new TrustManager[]{new X509TrustManagerImpl()}, null);

        SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = new BasicClientConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));

        DefaultHttpClient client = new DefaultHttpClient(ccm);
        java.lang.reflect.Field clientField = getClass().getSuperclass().getDeclaredField("restclient");
        clientField.setAccessible(true);
        clientField.set(this, new RestClient(client, credentials, URI.create(uri)));
    }
}
