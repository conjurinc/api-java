package net.conjur.api;

import net.conjur.api.authn.AuthnProvider;
import net.conjur.api.authn.TokenAuthFilter;
import net.conjur.util.logging.LogFilter;
import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.ContextResolver;
import java.net.URI;
import java.sql.ClientInfoStatus;
import java.util.logging.Logger;

public class Resource {
    private AuthnProvider authn;
    private Endpoints endpoints;
    private Client client;

    public Resource(AuthnProvider authn, Endpoints endpoints){
        this.authn = authn;
        this.endpoints = endpoints;
        client = createClient();
    }

    public Resource(Resource relative){
        this(relative.getAuthn(), relative.getEndpoints());
    }

    public AuthnProvider getAuthn() {
        return authn;
    }

    protected Endpoints getEndpoints() {
        return endpoints;
    }

    protected WebTarget target(URI uri){
        return client.target(uri);
    }

    protected Client client(){
        return client;
    }

    protected Client createClient(){
        ClientBuilder builder = ClientBuilder.newBuilder()
                .register(new TokenAuthFilter(authn))
                .register(JacksonFeature.class)
                .register(contextResolver);
        if(requestLoggingEnabled()){
            builder.register(new LogFilter());
        }
        return builder.build();
    }

    // TODO this is a stupid hack
    private static final boolean requestLoggingEnabled(){
        final String prop = System.getProperty("net.conjur.api.resource.requestLogging");
        if(prop != null && prop.toLowerCase().equals("true")){
            return true;
        }
        return false;
    }

    private final ContextResolver<ObjectMapper> contextResolver = new ContextResolver<ObjectMapper>() {
        public ObjectMapper getContext(Class<?> type) {
            final InjectableValues values = new InjectableValues.Std()
                    .addValue(Resource.class, Resource.this);
            return new ObjectMapper().setInjectableValues(values);
        }
    };
}