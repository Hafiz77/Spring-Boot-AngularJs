package mainApp.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Hafiz on 10/9/2016.
 */
@Service
public class TestService {
    @Value("${message:Hello World}")
    private String msg;

    public String message() {
        return this.msg;
    }
}
