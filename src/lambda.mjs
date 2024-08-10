import {PutObjectCommand, S3Client} from "@aws-sdk/client-s3";

async function putObjectToS3(bucket, key, data) {
    const client = new S3Client();
    const command = new PutObjectCommand({
        Bucket: bucket,
        Key: key,
        Body: data
    });
    try {
        await client.send(command)
        console.log("Successfully wrote to object", key);
    } catch (err) {
        console.error(err);
        console.error(err.stack);
        throw new TypeError("Something went wrong uploading the file, please retry!");
    }
}

/**
 * @typedef {Object} File
 * @property {string} name - The name of the file.
 * @property {string} content - The content of the file.
 */

/**
 * @typedef {Object} Body
 * @property {string} userid - The user ID.
 * @property {File} file - The file to be saved.
 */


export async function handler(event, context) {
    const bucket = "mindustry-remote-save-bucket";
    /**
     * @type {Body}
     */
    const body = event.body === undefined ? event : JSON.parse(event.body);
    console.log("Received request with body: ", body);
    const {userid, file} = body;
    if (!userid || !file) {
        return {
            statusCode: 400,
            body: "Missing userId or file!"
        }
    }
    await putObjectToS3(bucket, `saves/${userid}/${file.path}`, file.content);
    console.log("Everything went smoothly!");
    return {
        statusCode: 200,
        body: "Success!"
    };
}