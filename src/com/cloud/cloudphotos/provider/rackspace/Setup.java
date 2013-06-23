package com.cloud.cloudphotos.provider.rackspace;

import android.util.Log;

public class Setup {

    public static final String PREFS_KEY_HAS_ACCOUNT = "has_provider_rackspace";

    public Setup() {

    }

    /**
     * Retrieve the URL value from a 'readable' format of account endpoint.
     * 
     * @param str
     * @return
     */
    public static String getAuthenticationEndpointFromString(String str) {

        String us = "https://identity.api.rackspacecloud.com/v1.0";
        if (str.equalsIgnoreCase("United Kingdom (LON)")) {
            return "https://lon.identity.api.rackspacecloud.com/v1.0";
        }

        return us;
    }

    /**
     * Basic validation of credentials as entered into the prompt.
     * 
     * @param username
     * @param apikey
     * @param endpoint
     * @return
     */
    public Boolean areValidCredentials(String username, String apikey, String endpoint) {
        Log.v("CloudPhotos", "Endpoint URL : " + getAuthenticationEndpointFromString(endpoint));
        if (username.isEmpty() || apikey.isEmpty() || endpoint.isEmpty()) {
            return false;
        }
        return true;
    }

}