package tw.chiae.inlive.presentation.ui.login;

import android.content.Context;

/*import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;*/

import java.io.IOException;

import tw.chiae.inlive.R;

/**
 * Created by rayyeh on 2017/3/17.
 */

public class PeopleHelper {
    /*public static People setup (Context context , String serverAuthCode) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport,
                jsonFactory,
                context.getString(R.string.default_web_client_id),
                context.getString(R.string.clientSecret),
                serverAuthCode,
                redirectUrl).execute();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(context.getString(R.string.default_web_client_id), context.getString(R.string.clientSecret))
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .build();


        credential.setFromTokenResponse(tokenResponse);

        return new People.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("inLive")
                .build();
    }*/
}
