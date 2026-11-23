// this is the test class for the CustomUserDetailsService class

package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.Users;
import com.phollyerviggiani.athleteguard.security.CustomUserDetails;
import com.phollyerviggiani.athleteguard.repository.UsersRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    // test #1: successfully load the user by email
    @Test
    void shouldLoadUserByEmail() {

        // ARRANGE

        Users user = new Users();
        user.setUserId(1);
        user.setEmail("athlete@test.com");
        user.setPasswordHash("$2a$12$hashedpassword");
        user.setRole("ATHLETE");

        when(usersRepository.findByEmail("athlete@test.com"))
                .thenReturn(Optional.of(user));

        // ACT

        CustomUserDetails result = (CustomUserDetails)
                customUserDetailsService.loadUserByUsername("athlete@test.com");

        // ASSERT

        assertNotNull(result);
        assertSame(user, result.getUser());

        verify(usersRepository).findByEmail("athlete@test.com");
    }

    // test #2: throw exception when user does not exist
    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        // ARRANGE

        String email = "unknown@test.com";

        when(usersRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // ACT & ASSERT

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );

        verify(usersRepository).findByEmail(email);
    }

    // test #3: return CustomUserDetails with correct authorities
    @Test
    void shouldReturnCustomUserDetails() {

        // ARRANGE

        Users user = new Users();
        user.setEmail("coach@test.com");
        user.setPasswordHash("$2a$12$hashedpassword");
        user.setRole("COACH");

        when(usersRepository.findByEmail("coach@test.com"))
                .thenReturn(Optional.of(user));

        // ACT

        var result = customUserDetailsService.loadUserByUsername("coach@test.com");

        // ASSERT

        assertInstanceOf(CustomUserDetails.class, result);

        verify(usersRepository).findByEmail("coach@test.com");
    }

    // test #4: return correct username from CustomUserDetails
    @Test
    void shouldReturnCorrectUsername() {

        // ARRANGE

        Users user = new Users();
        user.setEmail("athlete@test.com");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // ACT

        String result = userDetails.getUsername();

        // ASSERT

        assertEquals("athlete@test.com", result);
    }

    // test #5: return correct password from CustomUserDetails
    @Test
    void shouldReturnCorrectPassword() {

        // ARRANGE

        Users user = new Users();
        user.setPasswordHash("$2a$12$hashedpassword");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // ACT

        String result = userDetails.getPassword();

        // ASSERT

        assertEquals("$2a$12$hashedpassword", result);
    }

    // test #6: return correct role from CustomUserDetails
    @Test
    void shouldReturnCorrectRole() {

        // ARRANGE

        Users user = new Users();
        user.setRole("COACH");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // ACT

        Collection<? extends GrantedAuthority> authorities =
                userDetails.getAuthorities();

        // ASSERT

        assertEquals(1, authorities.size());
        assertEquals("ROLE_COACH", authorities.iterator().next().getAuthority());
    }

    // test #7: return true for all account status checks
    @Test
    void shouldReturnTrueForAllAccountStatusChecks() {

        // ARRANGE

        Users user = new Users();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // ACT & ASSERT

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}