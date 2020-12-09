package com.codehub;

/**
 * Sample Java code for youtube.videos.insert
 * See instructions for running these code samples locally:
 * https://developers.google.com/explorer-help/guides/code_samples#java
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

public class UploadNew {
    private static final String CLIENT_SECRETS= "/client_secrets.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.upload");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = YoutubeList.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow,
                        new LocalServerReceiver.Builder().setHost("localhost").setPort(8080).setCallbackPath("/Callback").build())
                        .authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();

        // Define the Video object, which will be uploaded as the request body.
        Video video = new Video();

        // Add the snippet object property to the Video object.
        VideoSnippet snippet = new VideoSnippet();
        snippet.setCategoryId("22");
        snippet.setDescription("Description of uploaded video.");
        snippet.setTitle("Test video upload.");
        video.setSnippet(snippet);

        // Add the status object property to the Video object.
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("private");
        video.setStatus(status);

        // TODO: For this request to work, you must replace "YOUR_FILE"
        //       with a pointer to the actual file you are uploading.
        //       The maximum file size for this operation is 137438953472.
        // https://developers.google.com/youtube/v3/docs/videos/insert?apix=true
       // TODO: https://stackoverflow.com/questions/2376397/turning-a-remote-file-into-an-input-stream-over-http-in-java


//        File mediaFile = new File("/Users/ddcgordonz/Projects/youtubeUploader/src/main/java/com/codehub/sample-video.mp4");
//
//        InputStreamContent mediaContent =
//                new InputStreamContent("application/octet-stream",
//                        new BufferedInputStream(new FileInputStream(mediaFile)));
//

        URL url = UploadNew.class.getResource("/sample-video.mp4");
        File mediaFile = new File(url.getPath());

        InputStreamContent mediaContent =
        new InputStreamContent("application/octet-stream",
                new BufferedInputStream(new FileInputStream(mediaFile)));

//        InputStreamContent mediaContent = new InputStreamContent("application/octet-stream",
//                new BufferedInputStream(UploadVideo.class.getResourceAsStream("/sample-video.mp4")));
//
        mediaContent.setLength(mediaFile.length());

        // Define and execute the API request
        YouTube.Videos.Insert request = youtubeService.videos()
                .insert(Arrays.asList( "snippet","status"), video, mediaContent);
        Video response = request.execute();
        System.out.println(response);
    }
}