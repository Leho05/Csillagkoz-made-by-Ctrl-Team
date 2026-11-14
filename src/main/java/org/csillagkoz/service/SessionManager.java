package org.csillagkoz.service;

import org.csillagkoz.model.User;

public class SessionManager {
    private static User currentUser;

    public static void login(User user) { currentUser = user; }
    public static void logout() { currentUser = null; }
    public static User getCurrentUser() { return currentUser; }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}