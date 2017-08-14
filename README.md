Conjur API for Java
===================

## Installing

### From Source

To build the library from source you'll need Maven.  You can build it like this:

```bash
git clone {repo}

cd conjur-api

mvn package

```

If you are using Maven to manage your project's dependencies, you can run `mvn install` to install the package locally, and then include following dependency in your `pom.xml`:

```xml
<dependency>
  <groupId>net.conjur.api</groupId>
  <artifactId>conjur-api</artifactId>
  <version>1.5</version>
</dependency>
```

If you aren't using Maven, you can add the `jar` in the normal way.  This `jar` can be found in
the `target` directory created when you ran `mvn package`.

Note that this will *not* run the integration tests, since these require access to a Conjur instance.  To run the
integration tests, you will need to define the following environment variables for the `mvn package` command:
```bash
CONJUR_ACCOUNT=accountName
CONJUR_CREDENTIALS=username:apiKey
CONJUR_APPLIANCE_URL=http://conjur
```

## Basic Usage

### Creating a Conjur Instance

A `Conjur` instance provides access to the individual Conjur services. To create one, you'll need the environment
variables as described above. You will typically create a Conjur instance from these values in the following way:

```java
Conjur conjur = new Conjur();

```

where the Conjur object is logged in to the account & ready for use.


### Variable Operations

A Variable is an access-controlled list of encrypted data values. The values in a Variable are colloquially known as ?secrets?.

You will typically add secrets to variables & retrieve secrets from variables in the following way:

```java
Conjur conjur = new Conjur();

conjur.variables().addSecret(VARIABLE_KEY, VARIABLE_VALUE);

String retrievedSecret = conjur.variables().retrieveSecret(VARIABLE_KEY);
```


### SSL Certificates

By default, the Conjur appliance generates and uses self-signed SSL certificates. You'll need to configure
Java to trust them. You can accomplish this by loading the Conjur certificate into the Java keystore.
First, you'll need a copy of this certificate, which you can get using the [Conjur CLI](https://try.conjur.org/installation/client.html).
Once you've installed the command line tools, you can run

```bash
conjur init
```

and enter the required information at the prompts.  This will save the certificate to a file like `"conjur-mycompany.pem"`
in your HOME directory.  Java doesn't deal with the *pem* format, so next you'll need to convert it to the *der* format:

```bash
openssl x509 -outform der -in conjur-yourcompany.pem -out conjur-yourcompany.der
```

Next, you'll need to locate your JRE home.   On my machine it's `/usr/lib/jvm/java-7-openjdk-amd64/jre/`.  We'll export
this path to $JRE_HOME for convenience. If the file `$JRE_HOME/lib/security/cacerts` doesn't exist (you might need to be
root to see it), you've got the wrong path for your JRE_HOME.  Once you've found it, you can add the appliance's cert
to your keystore like this:

```bash
keytool -import -alias conjur-youraccount -keystore "$JRE_HOME/lib/security/cacerts"  -file ./conjur-youraccount.der
```


## JAXRS Implementations

The Conjur API client uses the JAXRS standard to make requests to the Conjur web services.  In the future we plan to
remove this dependency, but for the time being you may need to change the JAXRS implementation to conform to your
environment and application dependencies.  For example, in a JBoss server environment, you should use the RESTlet
implementation.  The Conjur API uses Apache CFX by default.  You can replace that dependency in `pom.xml` to use an
alternative implementation.

## License

Copyright 2016-2017 CyberArk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this software except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
