package pt.isec.pd.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.models.Event;
import pt.isec.pd.models.database.DatabaseManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("event")
public class EventsController {
    //GET: localhost:8080/event/list
    @GetMapping("/list")
    public ResponseEntity list(
            Authentication authentication,
            @RequestParam(value="start_time", required=false) String startTime,
            @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "date",required = false)String date,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "location", required = false) String location
    ) throws UnsupportedEncodingException {
        Jwt acc_details = (Jwt) authentication.getPrincipal();

        if(!acc_details.getClaim("scope").toString().equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must be a Admin User to call this action!");
        if(startTime==null&&endTime==null&&date==null&&name==null&&location==null){
            return ResponseEntity.ok(DatabaseManager.getInstance().getAllEvents());
        }
        Event evt = new Event(Event.type_event.LIST_CREATED_EVENTS, 0);
        if (startTime != null && !startTime.isEmpty()) {
            if (endTime == null || endTime.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Need end_time=HH:mm parameter");
            }
            evt.setEvent_end_time(endTime);
            evt.setEvent_start_time(startTime);
        } else if (endTime != null && !endTime.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Need start_time=HH:mm parameter");
        }
        if(date!=null){
            if(!date.isBlank()||!date.isEmpty()){
                evt.setEvent_date(date);
            }
        }
        if(name!=null){
            String event_name = URLDecoder.decode(name, StandardCharsets.UTF_8.toString());
            if(!name.isBlank()||!name.isEmpty()){
                evt.setEvent_name(event_name);
            }
        }
        if(location!=null){
            if(!location.isBlank()||!location.isEmpty()){
                evt.setEvent_location(location);
            }
        }
        return ResponseEntity.ok(DatabaseManager.getInstance().getCreatedEvents(evt));
    }



    //POST: localhost:8080/event/create/name={name}/location={location}/date={date}/start_time={start_time}/end_time={end_time}
    //Example:  localhost:8080/event/create/name=Aula PD/location=Lab1.1/date=06-15-2023/start_time=16:00/end_time=19:00

    @PostMapping("/create/name={name}/location={location}/date={date}/start_time={start_time}/end_time={end_time}")
    public ResponseEntity event(
            Authentication authentication,
            @PathVariable("name") String name,
            @PathVariable("location") String location,
            @PathVariable("date") String date,
            @PathVariable("start_time") String startTime,
            @PathVariable("end_time") String endTime) throws UnsupportedEncodingException {

        Jwt acc_details = (Jwt) authentication.getPrincipal();

        if(!acc_details.getClaim("scope").toString().equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must be a Admin User to call this action!");

        String auth_username = acc_details.getSubject().toString();

        System.out.println("[*] Event Created by " + auth_username);

        // Decodificar o nome do evento
        String event_name = URLDecoder.decode(name, StandardCharsets.UTF_8.toString());
        Event evt = new Event(Event.type_event.CODE_EVENT, 0);
        evt.setEvent_name(event_name);
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

    //Delete: localhost:8080/event/delete/name={name}

    @DeleteMapping("/delete/name={name}")
    public ResponseEntity event(
            Authentication authentication,
            @PathVariable("name") String name) throws UnsupportedEncodingException {

        Jwt acc_details = (Jwt) authentication.getPrincipal();

        if (!acc_details.getClaim("scope").toString().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must be an Admin User to call this action!");
        }

        String auth_username = acc_details.getSubject().toString();

        System.out.println("[*] Event Deleted by " + auth_username);
        // Decodificar o nome do evento
        String event_name = URLDecoder.decode(name, StandardCharsets.UTF_8.toString());

        Event evt = new Event(Event.type_event.CODE_EVENT, 0);
        evt.setEvent_name(event_name);

        if (DatabaseManager.getInstance().deleteEvent(evt)) {
            return ResponseEntity.ok("Event Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete event ");
        }
    }

}
