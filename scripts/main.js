const fs = require("fs");

/**
 * @typedef {Object} File
 * @property {string} name - The name of the file.
 * @property {string} path - The path of the file.
 * @property {string} content - The content of the file.
 */

/**
 * @typedef {Object} Body
 * @property {string} userid - The user ID.
 * @property {File[]} files - An array of files.
 */

function getSaveFolderPath() {
  // Windows uses win32 for some reason.
  if (process.platform === "win32") {
    // If the game is purchased from Steam, the save files are located in the Steam directory.
    // Since Steam has remote saves, I will ignore it.
    return `${process.env.APPDATA}/Mindustry/saves`;
  }
  if (process.platform === "darwin") {
    return `${process.env.HOME}/Library/Application Support/Mindustry/saves`;
  }
  return `${process.env.HOME}/.local/share/Mindustry/saves`;
}

function getUserId() {
  return fs.readFileSync("./userId", "utf8");
}

function randomString(length) {
  let result = "";
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  const charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}

function createAndSaveUserIdIfNotExists() {
  if (!fs.existsSync("./userId")) {
    fs.writeFileSync("./userId", "user_v1_" + randomString(64));
  }
}

/**
 * Reads the save files.
 * @returns {File[]} - The save files.
 */
function readSaveFiles() {
  const saveFiles = fs.readdirSync(getSaveFolderPath());
  console.log(saveFiles);
  return saveFiles.map((fileName) => {
    const filePath = `${getSaveFolderPath()}/${fileName}`;
    const fileContent = fs.readFileSync(filePath, "utf8");
    const encodedFileContent = encodeURIComponent(fileContent);
    return {
      name: fileName,
      path: filePath,
      content: encodedFileContent,
    };
  });
}

/**
 * Uploads the files to S3 invoking a Lambda function.
 * @param {File[]} files - The files to upload.
 */
async function uploadFilesToS3(files) {
  createAndSaveUserIdIfNotExists();
  /**
   * @type {Body}
   */
  const body = {
    userid: getUserId(),
    files,
  };
  const res = await fetch(
    "https://rfgqiwd4bcgtpdljeqv2tqbsyi0zswra.lambda-url.eu-west-2.on.aws/",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    }
  );
  if (!res.ok) {
    console.error("Failed to upload files to S3.");
    console.error(await res.text());
  } else {
    console.log("Files uploaded to S3 correctly.");
  }
}

function main() {
  createAndSaveUserIdIfNotExists();
  uploadFilesToS3(readSaveFiles());
}

main();

// Run the main function every 5 minutes.
setInterval(main, 1000 * 60 * 5);
