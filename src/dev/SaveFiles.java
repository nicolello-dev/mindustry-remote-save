package dev;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SaveFiles {
    private List<SaveFile> saveFiles;

    public List<SaveFile> getSaveFiles() {
        return saveFiles;
    }

    public void setSaveFiles(List<SaveFile> saveFiles) {
        this.saveFiles = saveFiles;
    }

    public SaveFiles() {
    }

    public String getGameFolderPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        Map<String, String> env = System.getenv();
        if (osName.contains("win")) {
            return env.get("APPDATA") + "/Mindustry/";
        }
        if (osName.contains("mac") || osName.contains("darwin")) {
            return env.get("HOME") + "/Library/Application Support/Mindustry/";
        }
        return env.get("HOME") + "/.local/share/Mindustry/";
    }

    public String getSaveFolderPath() {
        return getGameFolderPath() + "saves/";
    }

    private Byte[] byteToByte(byte[] bytes) {
        Byte[] result = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }

    public void updateSaveFiles() throws ArcRuntimeException {

        String gameFolderPath = getGameFolderPath();
        String saveFolderPath = getSaveFolderPath();
        Fi saveFolder = new Fi(saveFolderPath);
        Seq<Fi> saveFilesAsFi = saveFolder.findAll();
        List<SaveFile> saveFiles = new ArrayList<>();
        // For each save file, create the corresponding `SaveFile` object and add it to the array
        for (Fi saveFile : saveFilesAsFi) {
            Log.info("Reading file: " + saveFile.name());
            SaveFile sf = new SaveFile();
            sf.setFileName(saveFile.name());
            // To get the file's relative path, remove the game folder's absolute path from the file's absolute path.
            int gameFolderPathLength = gameFolderPath.length();
            String saveFileRelativePath = saveFile.absolutePath().substring(gameFolderPathLength);
            sf.setRelativeFilePath(saveFileRelativePath);
            sf.setSaveFileContents(byteToByte(saveFile.readBytes()));

            saveFiles.add(sf);
        }
        setSaveFiles(saveFiles);
    }

    public String[] getJSONSaveFiles() {
        if (saveFiles == null || saveFiles.isEmpty()) {
            throw new RuntimeException("saveFiles is null or empty");
        }
        return saveFiles.stream().map(SaveFile::toJSON).toArray(String[]::new);
    }
}
