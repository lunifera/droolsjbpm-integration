package org.kie.remote.client.documentation;

import java.net.MalformedURLException;
import java.net.URL;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.services.client.api.RemoteRestRuntimeEngineFactory;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;

public class MigrationFrom6_0or6_1To6_2Example {

    /**
     * In the 6.0.x code, we did this: 
     * 
     *  // Setup remote JMS runtime engine factory
     *  RemoteJmsRuntimeEngineFactory remoteJmsFactory 
     *      = new RemoteJmsRuntimeEngineFactory(deploymentId, serverUrl, user, password);
     *
     *  // Create runtime engine
     *  RuntimeEngine engine = remoteJmsFactory.newRuntimeEngine();
     */
    public void createJmsRuntimeEngine(String deploymentId, URL serverUrl, String user, String password) { 
        // Now we just do this: 
        RemoteRuntimeEngine engine = RemoteRuntimeEngineFactory.newJmsBuilder()
                .addDeploymentId(deploymentId)
                .addJbossServerUrl(serverUrl)
                .addUserName(user)
                .addPassword(password)
                .build();
        
        // If you still want to use the factory to create multiple instances, you can always still do this: 
        RemoteRuntimeEngineFactory jmsRuntimeFactory = RemoteRuntimeEngineFactory.newJmsBuilder()
                .addDeploymentId(deploymentId)
                .addJbossServerUrl(serverUrl)
                .addUserName(user)
                .addPassword(password)
                .buildFactory();
       
        engine = jmsRuntimeFactory.newRuntimeEngine();
    }
   
    /**
     * With the introduction of the fluent/builder API in 6.0.x, we could do this: 
     * 
     *   RemoteRestRuntimeEngineFactory factory = 
     *          RemoteRestRuntimeEngineFactory.newBuilder()
     *          .addUserName("user")
     *          .addPassword("pass")
     *          .addUrl(new URL("http://localhost:8080/business-central"))
     *          .build();
     *  
     *   RuntimeEngine runtimeEngine = factory.newRuntimeEngine();
     *   
     * With the 6.0.x code, we built a factory, that we then used to create the runtime.. 
     * 
     *  Why not just build the runtime? 
     */
    public void createRestRuntimeEngineWithBuilder() throws MalformedURLException { 
        // unfortunately, this breaks backward compatibility, but the improvement and clarity in the API is worth the risk
        RemoteRuntimeEngine runtimeEngine = 
               RemoteRestRuntimeEngineFactory.newBuilder()
               .addUserName("user")
               .addPassword("pass")
               .addUrl(new URL("http://localhost:8080/business-central"))
               .build();
       
    }
}
