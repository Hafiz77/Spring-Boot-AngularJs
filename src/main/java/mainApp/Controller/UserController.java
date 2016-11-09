package mainApp.Controller;

import com.google.gson.Gson;
import mainApp.Models.User;
import mainApp.Services.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by Hafiz on 10/25/2016.
 */

@Controller
@Path("/users")
public class UserController {
    UserService userService = new UserService();
    static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private Gson gson = new Gson();
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response create(User user) {
        String UserJson = gson.toJson(user);
        logger.debug(">> create({})", UserJson);
        LinkedHashMap<Object, Object> serviceResponse = new LinkedHashMap<Object, Object>();
        logger.info("Starting to create a user");

        try {
            Set<ConstraintViolation<User>> validateErrors = validator.validate(user);
            if (validateErrors.isEmpty()) {
                User createPerson = userService.createUser(user);
                if (createPerson == null) {
                    serviceResponse.put("created", "unable to create user");
                } else {
                    logger.info("Successfully created User.");
                    serviceResponse.put("created", createPerson);
                }
                return Response.status(Response.Status.OK).entity(serviceResponse).build();
            } else {
                logger.info("Failed to create a user due to field validation errors.");
                logger.debug("Unable to create a user due to validation errors using {}", user);
                //JSONObject jsonObj = new JSONObject(validateErrors.toString());
                serviceResponse.put("error", validateErrors.toString());
                return Response.status(400).entity(serviceResponse).build();
            }
        } catch (Exception e) {

        }
        logger.debug("<< create()");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serviceResponse).build();
    }

    @GET
    @Produces("application/json")
    public Response getUsers() {

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        try {
            List<User> listPersons = userService.listUsers();
            if (listPersons == null) {
                response.put("users", Collections.emptyMap());
            } else {
                response.put("total", listPersons.size());
                response.put("persons", listPersons);
            }

            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception ex) {

        }
        response.put("user", "Not Found");

        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }


    @PUT
    @Path("/{id}")
    @Produces("application/json")
    public Response updateUser(User user) {
        String personJson = gson.toJson(user);
        logger.debug(">> create({})", personJson);
        //LinkedHashMap<Object, Object> apiResponse = new LinkedHashMap<>();
        LinkedHashMap<Object, Object> serviceResponse = new LinkedHashMap<Object, Object>();
        logger.info("Starting to create a person");

        try {
            Set<ConstraintViolation<User>> validateErrors = validator.validate(user);
            if (validateErrors.isEmpty()) {
                int updateUser = userService.updateUser(user);


                if (updateUser == 0) {
                    serviceResponse.put("created", "unable to update User");
                } else {
                    logger.info("Successfully update user.");

                    serviceResponse.put("update", user);
                }
                return Response.status(Response.Status.OK).entity(serviceResponse).build();
            } else {
                logger.info("Failed to update a user due to field validation errors.");
                logger.debug("Unable to update a user due to validation errors using {}", personJson);
                serviceResponse.put("error", validateErrors.toString());

                return Response.status(400).entity(serviceResponse).build();
            }
        } catch (Exception e) {

        }
        logger.debug("<< create()");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serviceResponse).build();
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getPerson(@PathParam("id") int userId) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

        try {
            User user = userService.getUser(userId);

            if (user == null) {
                response.put("User", Collections.emptyMap());
            } else {
                response.put("person", user);
            }
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception ex) {

        }
        response.put("user", "Not Found");

        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response deleteUser(@PathParam("id") int userId) {

        //LinkedHashMap<Object, Object> apiResponse = new LinkedHashMap<>();
        LinkedHashMap<Object, Object> serviceResponse = new LinkedHashMap<Object, Object>();
        logger.info("Starting to delete a person");

        try {
            int deletePerson = userService.deleteUser(userId);


            if (deletePerson == 0) {
                serviceResponse.put("delete", "unable delete person");
            } else {
                logger.info("Successfully delete person.");

                serviceResponse.put("delete", "Successfully delete person.");
            }
            return Response.status(Response.Status.OK).entity(serviceResponse).build();

        } catch (Exception e) {

        }
        logger.debug("<< create()");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serviceResponse).build();
    }

    @GET
    @Path("/excel")
    @Consumes("application/json")
    @Produces("application/json")
    public Boolean exportExcel() {

        //LinkedHashMap<Object, Object> apiResponse = new LinkedHashMap<>();
        LinkedHashMap<Object, Object> serviceResponse = new LinkedHashMap<Object, Object>();
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        logger.info("Starting to delete a person");
        List<User> users = userService.listUsers();

        Workbook xlsImport = new XSSFWorkbook();
        XSSFWorkbook workbook = new XSSFWorkbook();
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Employee Data");
        for (int i = 0; i < users.size(); i++) {
            data.put(i + "", new Object[]{users.get(i).getId(), users.get(i).getFirstname(), users.get(i).getLastname(), users.get(i).getEmail(), users.get(i).getPhone(), users.get(i).getCell(), users.get(i).getAge()});
        }
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String) {
                    System.out.println("Test");
                    cell.setCellValue(obj.toString());
                } else if (obj instanceof Integer) {
                    System.out.println("Another Test");
                    cell.setCellValue((Integer) obj);
                }
            }
        }


        try {
            FileOutputStream out = new FileOutputStream(new File("E:\\Excell\\user_demo.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("user_demo.xlsx written successfully on disk.");

        } catch (Exception e) {
            System.out.println("Error to Export Excel");
        }
        logger.debug("<< create()");
        return true;
    }
}
