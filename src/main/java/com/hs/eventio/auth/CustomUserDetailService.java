package com.hs.eventio.auth;

import com.hs.eventio.common.GlobalDTO;
import com.hs.eventio.user.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDTO = userService.findUserByUsername(username);
        return new User(userDTO.name(), userDTO.password(), getAuthorities(userDTO));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            GlobalDTO.FindUserResponse userDTO) {
        var grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (var role: userDTO.roles()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.authority()));
        }
        return grantedAuthorities;
    }
}
