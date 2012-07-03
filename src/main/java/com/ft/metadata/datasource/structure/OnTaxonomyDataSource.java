package com.ft.metadata.datasource.structure;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;

import com.eidosmedia.datasource.Collection;
import com.eidosmedia.datasource.DSException;
import com.eidosmedia.datasource.Datasource;

public class OnTaxonomyDataSource implements Datasource {
	private final static HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	private final HttpClient client = new HttpClient(connectionManager);

	private static final String CLIENT_USER_PRINCIPAL_HEADER = "ClientUserPrincipal";
	private static final String STRUCTURE_SERVICE_URI_TEMPLATE = "http://%s:%d/metadata-services/structure/1.0/search?inflate=MAX";

	public static final String REALM_PARAM = "realm";
	public static final String HOST_PARAM = "host";
	public static final String PORT_PARAM = "port";
	public static final String USERNAME_PARAM = "username";
	public static final String PASSWORD_PARAM = "password";
	public static final String PRINCIPAL_PARAM = "principal";

	private final String realm;
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String principal;

	public OnTaxonomyDataSource(final Map<String, String> params) {		
		this.realm = params.get(REALM_PARAM);
		this.host = params.get(HOST_PARAM);
		this.username = params.get(USERNAME_PARAM);
		this.password = params.get(PASSWORD_PARAM);
		this.principal = params.get(PRINCIPAL_PARAM);
		this.port = Integer.parseInt(params.get(PORT_PARAM));
	}

	public Collection getCollection() throws DSException {
		return new OnTaxonomyCollection(this);
	}

	public void close() throws DSException {
	}

	public HttpState createAuthenticationState() {
		final AuthScope scope = new AuthScope(host, port, realm);
		final Credentials credentials = new UsernamePasswordCredentials(username, password);
		final HttpState state = new HttpState();
		state.setCredentials(scope, credentials);
		return state;
	}
	
	public PostMethod createRequest() throws UnsupportedEncodingException {
		final PostMethod post = new PostMethod(String.format(STRUCTURE_SERVICE_URI_TEMPLATE, host, port));
		post.addRequestHeader(CLIENT_USER_PRINCIPAL_HEADER, principal);
		return post;
	}
	
	public HttpClient getClient() {
		return client;
	}
}