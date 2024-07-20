package com.springboot.auth.utils;

import com.springboot.auth.userDetails.MemberDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminMailAddress;

    private final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN","ROLE_USERS");
    private final List<GrantedAuthority> USER_ROLES=AuthorityUtils.createAuthorityList("ROLE_USERS");
    private final List<String>ADMIN_ROLES_STRING = List.of("ADMIN","USER");
    private final List<String>USER_ROLES_STRING = List.of("USER");

    public List<GrantedAuthority>createAuthority(String email){
        if(email.equals(adminMailAddress)){
            return ADMIN_ROLES;
        }
        return USER_ROLES;
    }

    public List<GrantedAuthority>createAuthorities(List<String>roles){
        return roles.stream()
                .map(role-> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }

    public List<String>createRoles(String email){
        if(email.equals(adminMailAddress)){
            return ADMIN_ROLES_STRING;
        }
        return USER_ROLES_STRING;
    }
}
