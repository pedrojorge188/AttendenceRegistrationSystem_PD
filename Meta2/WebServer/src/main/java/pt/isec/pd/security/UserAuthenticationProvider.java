package pt.isec.pd.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pt.isec.pd.models.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider
{
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if(DatabaseManager.getInstance().userExists(username, password).equals("admin")){

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
            return new UsernamePasswordAuthenticationToken(username, password, authorities);

        } else if (DatabaseManager.getInstance().userExists(username, password).equals("normal")) {

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("UTIL"));
            return new UsernamePasswordAuthenticationToken(username, password, authorities);

        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
