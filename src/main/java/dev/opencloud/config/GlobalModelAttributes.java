package dev.opencloud.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Thymeleaf 3.1 (bundled with Spring Boot 3.2+) removed the "#request"
 * expression object from the default configuration. Templates that used
 * "${#request.getRequestURI()}" (e.g. to highlight the active sidebar
 * link) throw a TemplateProcessingException on every page render.
 *
 * This makes the current URI available to every view as "currentUri"
 * without needing "#request", and without having to touch every
 * individual controller.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
