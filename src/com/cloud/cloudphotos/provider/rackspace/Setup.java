package com.cloud.cloudphotos.provider.rackspace;

import android.util.Log;

public class Setup {

    public static final String PREFS_KEY_HAS_ACCOUNT = "has_provider_rackspace";
    public static final String PREFS_USER_USERNAME = "auth_rackspace_username";
    public static final String PREFS_USER_APIKEY = "auth_rackspace_apikey";
    public static final String PREFS_AUTH_TOKEN = "auth_rackspace_token";
    public static final String PREFS_URL_ENDPOINT = "auth_rackspace_url_auth";
    public static final String PREFS_URL_STORAGE = "auth_rackspace_url_storage";
    public static final String PREFS_CONTAINER_NAME = "auth_rackspace_container_name";

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