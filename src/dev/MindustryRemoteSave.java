package dev;

import arc.*;
import arc.func.Cons;
import arc.func.ConsT;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class MindustryRemoteSave extends Mod {
    String lambdaAPIUrl = "https://f0zfn12gx6.execute-api.eu-west-2.amazonaws.com/prod";
    String[] JSONSaveFiles;

    public MindustryRemoteSave() {
        Log.info("Loaded constructor.");

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                // Save user data
                User user = new User();
                user.createAndSaveUserIdIfNotExists();

                SaveFiles saveFiles = new SaveFiles();
                try {
                    saveFiles.updateSaveFiles();
                    Log.info("Successfully read all save files.");
                } catch (ArcRuntimeException err) {
                    Log.err("An error occurred while reading save files.");
                    Log.err(err.toString());
                    return;
                } catch (Exception err) {
                    Log.err("An unknown error occurred while reading save files.");
                    Log.err(err.toString());
                }

                this.JSONSaveFiles = saveFiles.getJSONSaveFiles();
                System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2"); // Force new TLS protocol
                System.setProperty("jsse.enableSNIExtension", "false"); // Disable SNIs
                updateFileSeqAsync(0, user);

                setInterval(() -> updateFileSeqAsync(0, user), 30);
            });
        });
    }

    private void updateFileSeqAsync(int index, User user) {
        // Don't do anything if files have finished
        if (index >= JSONSaveFiles.length) {
            Log.info("Finished uploading files");
            return;
        }
        if (index == 0) {
            Log.info("Starting to upload files");
        }
        updateFile(JSONSaveFiles[index], user, success -> {
            Log.info("Successfully uploaded file");
            updateFileSeqAsync(index + 1, user);
        });
    }

    private void updateFile(String JSONFile, User user, ConsT<Http.HttpResponse, Exception> callback) {
        String body = "{\"userid\":\"" + user.getUserId() + "\",\"file\":" + JSONFile + "}";
        Http.post(lambdaAPIUrl, body)
                .header("Content-Type", "application/json")
                .timeout(0)
                .error(err -> {
                    Log.info("Error in request with body: " + body);
                    Log.err(err.toString());
                })
                .submit(callback);
    }

    private void setInterval(Runnable callback, float delaySeconds) {
        Timer.schedule(callback, 0, delaySeconds);
    }

}
