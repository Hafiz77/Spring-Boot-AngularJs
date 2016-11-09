package mainApp;

import mainApp.Controller.TestController;
import mainApp.Controller.UserController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

/**
 * Created by Hafiz on 10/9/2016.
 */

@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(TestController.class);
        register(UserController.class);
    }

}
