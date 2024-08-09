package dev;

import arc.math.*;

public class User {

    private String userId;

    public User() {

    }

    private String randomString(int length) {
        StringBuilder result = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int index = Mathf.random(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    private void createAndSaveUserIdIfNotExists() {
        String userDataRelativePath = "./userId";

    }
}
