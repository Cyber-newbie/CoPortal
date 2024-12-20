package co.portal.logging_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity-logs")
public class LoggingController {

    @PostMapping("/save")
    public void storeLogsToDB(){
        return ;
    }



}
