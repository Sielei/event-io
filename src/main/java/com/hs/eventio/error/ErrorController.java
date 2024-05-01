package com.hs.eventio.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/errors")
class ErrorController {
    @GetMapping("/resource-not-found")
    public String getResourceNotFoundErrorPage(){
        return "resource-not-found";
    }
}
