package dev;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class MindustryRemoteSave extends Mod {

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
                    Log.err(err);
                    showBasicDialog("Something went wrong reading save files. Please try again or report it on github.");
                    return;
                }

                String files = saveFiles.getJSONSaveFiles();
                String lambdaAPIUrl = "https://rfgqiwd4bcgtpdljeqv2tqbsyi0zswra.lambda-url.eu-west-2.on.aws/";
                try {
                    Http.HttpRequest req = Http.post(lambdaAPIUrl, "{\"userid\":\"test_user\",files:" + files + "}");
                    req.timeout = 10000; // 10s
                    req.error(err -> {
                        Log.err("An error occurred while reading save files.");
                        Log.err(err);
                        throw new RuntimeException("An error occurred while POSTing request to API");
                    });
                    req.submit(r -> Log.info("Successfully got result: " + r.getResultAsString()));
                    Log.info("Successfully saved all data.");
                    // showBasicDialog("Saved all data.");
                } catch (Exception err) {
                    Log.err("An error occurred with the API request. Review any settings and proxies and retry.");
                    Log.err(err);
                    // showBasicDialog("Couldn't reach the server, please try again");
                }
            });
        });
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
