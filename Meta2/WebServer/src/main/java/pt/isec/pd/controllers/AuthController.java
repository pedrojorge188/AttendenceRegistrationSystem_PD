package pt.isec.pd.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.models.database.DatabaseManager;
import pt.isec.pd.security.TokenService;

@RestController
public class AuthController {
    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @PostMapping("/login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    //POST: localhost:8080/permission -> devolve true ou false conforme a role JWT da conta indicada

    @GetMapping("/permission")
    public ResponseEntity permission(Authentication authentication) {
        Jwt acc_details = (Jwt) authentication.getPrincipal();
       if(acc_details.getClaim("scope").toString().equals("ADMIN"))
           return ResponseEntity.ok().body("Admin");
       else
           return ResponseEntity.badRequest().body("Util");
    }

    //POST: localhost:8080/register/id={studentId}&username={username}&email={email}&password={password}

    @PostMapping("/register/id={studentId}&username={username}&email={email}&password={password}")
    public ResponseEntity register(
            @PathVariable("studentId") int id,
            @PathVariable("username") String username,
            @PathVariable("email") String email,
            @PathVariable("password") String password) {

            System.out.println("[*] Register attempt ( Id:"+id+"\tusername:"+username+"\temail:"+email+"\tpassword:"+password+")");
        if (DatabaseManager.getInstance().userCreate(username,id, email, password)) {
            return ResponseEntity.ok("User created");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
        }
    }
}