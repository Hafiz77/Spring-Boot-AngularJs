package mainApp.Controller;

import mainApp.Services.TestService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hafiz on 10/9/2016.
 */
@Path("/test")
public class TestController {
    @Autowired
    private TestService  testService;

    @GET
    @Path("/msg")
    @Produces("application/json")
    public Response message() {
        Map<Object, Object> apiResponse = new HashMap<Object, Object>();
        apiResponse.put("apiresponse", this.testService.message());
        return Response.ok(apiResponse).build();

    }
}
