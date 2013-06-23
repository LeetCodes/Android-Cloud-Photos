package com.cloud.cloudphotos.helper;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class SslFactory extends SSLSocketFactory {

    SSLContext sslContext = SSLContext.getInstance("TLS");

    public SslFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
            UnrecoverableKeyException {
        super(truststore);

        TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[] { trustManager }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
            UnknownHostException {
        Socket newSocket = sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        return newSocket;
    }

    @Override
    public Socket createSocket() throws IOException {
        Socket newSocket = sslContext.getSocketFactory().createSocket();
        return newSocket;
    }

}