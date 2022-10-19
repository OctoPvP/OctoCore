package net.octopvp.aetheriacoremaster.controllers.api;

import com.google.gson.JsonObject;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class APIErrorController implements ErrorController {
    //@RequestMapping("/api/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 500;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);
        String errorMessage = "";
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
            errorMessage = switch (statusCode) {
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Not Found";
                case 500 -> "Internal Server Error";
                default -> "Unknown Error";
            };
        } else {
            errorMessage = null;
        }
        jsonObject.addProperty("code", statusCode);
        jsonObject.addProperty("message", errorMessage);
        //return ResponseEntity.status(statusCode).body(AetheriaCoreCommon.getInstance().getGson().toJson(jsonObject));
        return AetheriaCoreCommon.getInstance().getGson().toJson(jsonObject);
    }
}
