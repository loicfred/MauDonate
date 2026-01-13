package mau.donate.service;

import mau.donate.objects.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class OAuth2Service extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(request);
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        String registrationId = request.getClientRegistration().getRegistrationId();

        User user = User.getByEmail(email);
        if (user == null) {
            user = new User(email,
                    UUID.randomUUID().toString(),                  // password
                    firstName,
                    lastName,
                    "USER",
                    null,                  // address
                    null,                  // gender
                    null,                  // phone
                    null,                  // dateOfBirth
                    false                  // isAnonymous
            );
            user.AccountProvider = registrationId;
            user.Enabled = true;
            user.Verified = true;
            user.Update();
        }

        return new DefaultOAuth2User(Collections.emptyList(), oauthUser.getAttributes(), "email");
    }

}
