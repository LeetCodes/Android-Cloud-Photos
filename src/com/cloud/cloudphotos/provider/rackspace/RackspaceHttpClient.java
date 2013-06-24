package com.cloud.cloudphotos.provider.rackspace;

import java.security.KeyStore;

import com.cloud.cloudphotos.helper.SslFactory;
import com.loopj.android.http.AsyncHttpClient;

public class RackspaceHttpClient {

    /**
     * Holds prebuilt AsyncHttpClient's
     */
    public RackspaceHttpClient() {

    }

    /**
     * Private method to retrieve a basic AsyncHttpClient
     * 
     * @return
     */
    private AsyncHttpClient getClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SslFactory sf = new SslFactory(trustStore);
            sf.setHostnameVerifier(SslFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        } catch (Exception e) {

        }
        return client;
    }

    /**
     * Retrieve a client ready for an authentication call
     * 
     * @param username
     * @param apikey
     * @return
     */
    public AsyncHttpClient getAuthenticationClient(String username, String apikey) {
        AsyncHttpClient client = getClient();
        client.addHeader("X-Auth-User", username);
        client.addHeader("X-Auth-Key", apikey);
        return client;
    }

    /**
     * Retrieve a prebuilt, authenticated ready HttpClient
     * 
     * @param storageToken
     * @return
     */
    public AsyncHttpClient getAuthenticatedStorageClient(String storageToken) {
        AsyncHttpClient client = getClient();
        client.addHeader("X-Storage-Token", storageToken);
        return client;
    }

}