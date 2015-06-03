package com.enlivenhq.slack;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class SlackWrapper
{
    protected String slackUrl;

    protected String username;

    protected String channel;

    public String send(String project, String buildNumber, String statusText, String statusColor, String message, String committers) throws IOException
    {
        String payloadText = project + " #" + buildNumber + " " + statusText;
        String attachmentProject = "{\"title\":\"Project\",\"value\":\"" + project + "\",\"short\": true}";
        String attachmentBuild = "{\"title\":\"Build\",\"value\":\"" + buildNumber + "\",\"short\": true}";
        String attachmentStatus = "{\"title\":\"" + statusText + "\",\"value\":\"" + message + "\",\"short\": false}";
        String committersField = "{\"title\":\"Committers\",\"value\":\"" + committers + "\",\"short\": false}";

        String formattedPayload = "payload={" +
            "\"text\":\"" + payloadText + "\"," +
            "\"attachments\": [{" +
                "\"fallback\":\"" + payloadText + "\"," +
                //"\"pretext\":\"Build Status\"," +
                "\"color\":\"" + statusColor + "\"," +
                "\"fields\": [" +
                    attachmentProject + "," +
                    attachmentBuild + "," +
                    attachmentStatus + "," +
                    committersField +
                "]" +
            "}]," +
            "\"channel\":\"" + this.getChannel() + "\"," +
            "\"username\":\"" + this.getUsername() + "\"" +
        "}";

        URL url = new URL(this.getSlackUrl());
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setRequestProperty("User-Agent", "Enliven");
        httpsURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        httpsURLConnection.setDoOutput(true);

        DataOutputStream dataOutputStream = new DataOutputStream(
                httpsURLConnection.getOutputStream()
        );

        dataOutputStream.writeBytes(formattedPayload);
        dataOutputStream.flush();
        dataOutputStream.close();

        InputStream inputStream;
        String responseBody = "";

        try {
            inputStream = httpsURLConnection.getInputStream();
        }
        catch (IOException e) {
            responseBody = e.getMessage() + ": ";
            inputStream = httpsURLConnection.getErrorStream();
            throw new IOException(getResponseBody(inputStream, responseBody));
        }

        return getResponseBody(inputStream, responseBody);
    }

    private String getResponseBody(InputStream inputStream, String responseBody) throws IOException {
        String line;

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );

        while ((line = bufferedReader.readLine()) != null) {
            responseBody += line + "\n";
        }

        bufferedReader.close();
        return responseBody;
    }


    public void setSlackUrl(String slackUrl)
    {
        this.slackUrl = slackUrl;
    }

    public String getSlackUrl()
    {
        return this.slackUrl;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return this.channel;
    }
}
