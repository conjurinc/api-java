package net.conjur.api.authn;

import net.conjur.api.Credentials;
import net.conjur.api.Endpoints;
import net.conjur.util.Args;
import net.conjur.util.HostNameVerification;
import net.conjur.util.rs.HttpBasicAuthFilter;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import static net.conjur.util.EncodeUriComponent.encodeUriComponent;

/**
 * Conjur authentication service client.
 * 
 * This client provides methods to get API tokens from the conjur authentication service,
 * which can then be used to make authenticated calls to other conjur services.
 *
 */
public class AuthnClient implements AuthnProvider {
    private final String username;
    private final String password;
    private final Endpoints endpoints;

    private Client client;
    private WebTarget root;
    private WebTarget login;
    private WebTarget authenticate;
    private WebTarget passwords;

	public AuthnClient(final String username,
                       final String password,
                       final Endpoints endpoints) {
        this.username = Args.notNull(username, "Username");
        this.password = Args.notNull(password, "Password");
        this.endpoints = Args.notNull(endpoints, "Endpoints");

        init();
	}

    public AuthnClient(final String username, final String password){
        this(username, password, Endpoints.getDefault());
    }

    public AuthnClient(Credentials credentials) {
        this(credentials.getUsername(), credentials.getPassword());
    }

    public String getUsername(){
        return username;
    }

    public Token authenticate() {
        // POST users/<username>/authenticate with apiKey in body
        try{
            return Token.fromJson(
                    authenticate.request("application/json").post(Entity.text(password), String.class)
            );
        }catch(NotFoundException e){
            // shim to work around a conjur bug where authn returns 404 when given a non-existent user
            throw new NotAuthorizedException(e);
        }
     }

    // implementation of AuthnProvider method
    public Token authenticate(boolean useCachedToken) {
        return authenticate();
    }


    public String login(){
        return login.request("text/plain").get(String.class);
     }

    public void updatePassword(String password){
        passwords.request().put(Entity.text(password), String.class); // need to read the string for this to throw
    }

    private void init(){
        final ClientBuilder builder = ClientBuilder.newBuilder()
                .register(new HttpBasicAuthFilter(username, password));

        HostNameVerification.getInstance().updateClientBuilder(builder);


        client = builder.build();
        root = client.target(endpoints.getAuthnUri()).path("users");
        login = root.path("login");
        authenticate = root.path(encodeUriComponent(username)).path("authenticate");
        passwords = root.path("password");
    }

}
