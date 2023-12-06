package pt.isec.pd.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.models.Event;
import pt.isec.pd.models.database.DatabaseManager;

@RestController
@RequestMapping("event")
public class EventsController {

    //POST: localhost:8080/event/create/name={name}/location={location}/date={date}/start_time={start_time}/end_time={end_time}
    //Example:  localhost:8080/event/create/name=Aula PD/location=Lab1.1/date=06-15-2023/start_time=16:00/end_time=19:00

    @PostMapping("/create/name={name}/location={location}/date={date}/start_time={start_time}/end_time={end_time}")
    public ResponseEntity event(
            Authentication authentication,
            @PathVariable("name") String name,
            @PathVariable("location") String location,
            @PathVariable("date") String date,
            @PathVariable("start_time") String startTime,
            @PathVariable("end_time") String endTime){

        Jwt acc_details = (Jwt) authentication.getPrincipal();

        if(!acc_details.getClaim("scope").toString().equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must be a Admin User to call this action!");

        String auth_username = acc_details.getSubject().toString();

        System.out.println("[*] Event Created by " + auth_username);

        Event evt = new Event(Event.type_event.CODE_EVENT, 0);
        evt.setEvent_name(name);
        evt.setEvent_location(location);
        evt.setEvent_date(date);
        evt.setEvent_start_time(startTime);
        evt.setEvent_end_time(endTime);

        if (DatabaseManager.getInstance().creatEvent(evt)) {
            return ResponseEntity.ok("Event Created");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed creating event ");
        }

    }

    //POST: localhost:8080/event/delete/name={name}

    @PostMapping("/delete/name={name}")
    public ResponseEntity event(
            Authentication authentication,
            @PathVariable("name") String name){

        Jwt acc_details = (Jwt) authentication.getPrincipal();

        if(!acc_details.getClaim("scope").toString().equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must be a Admin User to call this action!");

        String auth_username = acc_details.getSubject().toString();

        System.out.println("[*] Event Deleted by " + auth_username);

        Event evt = new Event(Event.type_event.CODE_EVENT, 0);
        evt.setEvent_name(name);

        if (DatabaseManager.getInstance().deleteEvent(evt)) {
            return ResponseEntity.ok("Event Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete event ");
        }

    }
}
