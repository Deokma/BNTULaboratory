package by.bntu.laboratory.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(WebRequest webRequest) {
        Object status = webRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE, WebRequest.SCOPE_REQUEST);
        ModelAndView modelAndView = new ModelAndView();

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                modelAndView.setViewName("error/404");
            } else if (statusCode == 500) {
                modelAndView.setViewName("error/500");
            } else if (statusCode == 403) {
                modelAndView.setViewName("error/403");
            } else {
                modelAndView.setViewName("error/404");
            }
        } else {
            modelAndView.setViewName("error/default");
        }

        return modelAndView;
    }

    public String getErrorPath() {
        return "/error";
    }
}
