package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.config.JiraConfig;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.RestException;
import net.sf.json.JSON;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hacked jira-client
 */
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class NonVerifyingJiraClient extends JiraClient {

    private static final String SEARCH = "rest/api/2/search";

    JiraConfig config;

    public NonVerifyingJiraClient(JiraConfig config) {
        super(config.getUrl(), new BasicCredentials(config.getLogin(), config.getPassword()));

        this.config = config;
    }


    /**
     * Fake {@link RestClient} for {@link JiraClient} non verifying jira client
     * todo: use SSLConnectionSocketFactory instead of SSLSocketFactory
     */
    @PostConstruct
    public void hack() throws NoSuchAlgorithmException, KeyManagementException, NoSuchFieldException, IllegalAccessException {
        if (!config.isHack()) {
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
        clientField.set(this,
                new RestClient(client,
                        new BasicCredentials(config.getLogin(), config.getPassword()), URI.create(config.getUrl())));
    }
}
