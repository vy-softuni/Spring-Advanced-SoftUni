package app.mendnook.hub.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationErrorController implements ErrorController {

    @RequestMapping("/error")
    String error(HttpServletRequest request, Model model) {
        Object statusAttribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusAttribute == null ? 500 : Integer.parseInt(statusAttribute.toString());
        model.addAttribute("statusCode", status);
        model.addAttribute("errorTitle", status == 404 ? "Page not found" : "Something went wrong");
        model.addAttribute("errorMessage", status == 404
                ? "The page or record you requested does not exist."
                : "The application could not complete this request safely.");
        return "error/error";
    }
}
