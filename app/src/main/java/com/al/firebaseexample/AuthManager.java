package com.al.firebaseexample;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Collections;
import java.util.List;

public class AuthManager {
    private final List<AuthUI.IdpConfig> _authUIProviders = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());

    public List<AuthUI.IdpConfig> getAuthUIProviders() {
        return _authUIProviders;
    }

    public boolean isAuth() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public String getEmail() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        return null;
    }
}
