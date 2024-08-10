package dev;

import arc.*;
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

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            //show dialog upon startup
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("frog");
                dialog.cont.add("behold").row();
                //mod sprites are prefixed with the mod name (this mod is called 'dev-java-mod' in its config)
                dialog.cont.image(Core.atlas.find("mindustry-remote-saveFile-frog")).pad(20f).row();
                dialog.cont.button("I see", dialog::hide).size(100f, 50f);
                dialog.show();

                // END INITIAL MOD

                SaveFiles saveFiles = new SaveFiles();
                try {
                    saveFiles.updateSaveFiles();
                    Log.info("Successfully read all save files.");
                } catch (ArcRuntimeException err) {
                    Log.err("An error occurred while reading save files.");
                    Log.err(err.toString());
                    showBasicDialog("Something went wrong reading save files. Please try again or report it on github.");
                    return;
                } catch (Exception err) {
                    Log.err("An unknown error occurred while reading save files.");
                    Log.err(err.toString());
                }

                this.JSONSaveFiles = saveFiles.getJSONSaveFiles();
                System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2"); // Force new TLS protocol
                System.setProperty("jsse.enableSNIExtension", "false"); // Disable SNIs
                updateFileSeqAsync(0);
            });
        });
    }

    private void updateFileSeqAsync(int index) {
        // Don't do anything if files have finished
        if (index >= JSONSaveFiles.length) {
            return;
        }
        updateFile(JSONSaveFiles[index], success -> {
            Log.info("Successfully updated file.");
            updateFileSeqAsync(index + 1);
        });
    }

    private void updateFile(String JSONFile, ConsT<Http.HttpResponse, Exception> callback) {
        String body = "{\"userid\":\"test_user_client\",\"file\":" + JSONFile + "}";
        Http.post(lambdaAPIUrl, body)
                .header("Content-Type", "application/json")
                .timeout(0)
                .error(err -> {
                    Log.info("Error in request with body: " + body);
                    Log.err(err.toString());
                })
                .submit(callback);
    }

    private void showBasicDialog(String text) {
        BaseDialog dialog = new BaseDialog("Remote save");
        dialog.cont.add(text).row();
        dialog.cont.button("Ok", dialog::hide).size(100f, 50f);
        dialog.show();
    }

    @Override
    public void loadContent() {
        Log.info("Loading some dev content.");
    }

}
