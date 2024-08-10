package dev;

import arc.files.Fi;
import arc.math.*;
import arc.util.ArcRuntimeException;
import arc.util.Log;

public class User {

    private final String userId;

    public User() {
        userId = getUserId();
    }

    /**
     * Generates a random alphanumerical string of length `length`
     *
     * @param length The length of the returned string
     * @return A string of random letters of length `length`
     */
    private String randomString(int length) {
        StringBuilder result = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int index = Mathf.random(characters.length() - 1);
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    /**
     * Creates a new user id. Doesn't check if the user already has an id
     *
     * @return A new user id
     */
    private String createUserId() {
        return "v1_" + randomString(64);
    }

    /**
     * Gets the user's id. If it is not set, it will create it. It does not save it.
     *
     * @return the user's (possibly new) id
     */
    public String getUserId() {
        // If userId has already been set, return that
        if (userId != null) {
            return userId;
        }
        // Otherwise, read the user id file and return that
        String userIdFromFile = readUserIdFromFile();
        if (userIdFromFile != null) {
            return userIdFromFile;
        }
        // In case that hasn't been set, then create a new one
        return createUserId();
    }

    public String readUserIdFromFile() {
        SaveFiles sf = new SaveFiles();
        String gameFolderPath = sf.getGameFolderPath();
        Fi userDataFile = new Fi(gameFolderPath + "/userId");
        try {
            return userDataFile.readString();
        } catch (ArcRuntimeException e) {
            return null;
        }
    }

    /**
     * Creates and saves the user's id if it doesn't exist. If it does, it ignores everything
     */
    public void createAndSaveUserIdIfNotExists() {
        String userDataRelativePath = "/userId";
        SaveFiles sf = new SaveFiles();
        String gameFolderPath = sf.getGameFolderPath();
        Fi userDataFile = new Fi(gameFolderPath + userDataRelativePath);
        String userId = getUserId();
        try {
            // If it is able to read the file without problems, then the user id is already set.
            userDataFile.readString();
        } catch (Exception e) {
            Log.info("File does not exist, creating user id. To be sure, here's the error:");
            Log.err(e);
            try {
                userDataFile.writeString(userId);
            } catch (ArcRuntimeException e1) {
                Log.err("There was a problem writing the user id");
                Log.err(e1);
            }
        }
    }
}
