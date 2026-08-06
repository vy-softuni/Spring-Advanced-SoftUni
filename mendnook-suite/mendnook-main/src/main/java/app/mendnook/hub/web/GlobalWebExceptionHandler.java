package app.mendnook.hub.web;

import app.mendnook.hub.shared.DomainRuleException;
import app.mendnook.hub.shared.MissingRecordException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

    @ExceptionHandler(DomainRuleException.class)
    String handleDomainRule(DomainRuleException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        model.addAttribute("statusCode", HttpStatus.UNPROCESSABLE_ENTITY.value());
        model.addAttribute("errorTitle", "That action is not available");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/error";
    }

    @ExceptionHandler(MissingRecordException.class)
    String handleMissingRecord(MissingRecordException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", "Record not found");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    String handleTypeMismatch(MethodArgumentTypeMismatchException exception,
                              Model model,
                              HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.warn("Invalid path or request parameter: {}", exception.getName());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorTitle", "Invalid request value");
        model.addAttribute("errorMessage", "One of the supplied identifiers or values has an invalid format.");
        return "error/error";
    }

    @ExceptionHandler(FeignException.class)
    String handleMaterialsApiFailure(FeignException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        log.error("Materials API call failed with status {}", exception.status());
        model.addAttribute("statusCode", HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("errorTitle", "Materials service unavailable");
        model.addAttribute("errorMessage", "Start the materials application and try this action again.");
        return "error/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    String handleIllegalArgument(IllegalArgumentException exception, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.warn("Invalid operation: {}", exception.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorTitle", "Invalid operation");
        model.addAttribute("errorMessage", "The submitted operation could not be understood.");
        return "error/error";
    }
}
