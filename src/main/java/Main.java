package org.example.cs427group20;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.SlackPersona;
import com.ullink.slack.simpleslackapi.impl.SlackPersonaImpl;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;
import com.ullink.slack.simpleslackapi.SlackPresence;
import com.ullink.slack.simpleslackapi.SlackPreparedMessage;

import org.example.cs427group20.FetchingMessageHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * Starts the console UI for cs427group20-simple-slack-api-tests.
 * Provides some commands for exercising simple-slack-api and viewing outputs.
 * Adapted from edu.ncsu.csc326.coffeemaker
 */
public class Main {
    private static SlackSession session;
    private static SlackChannel channel;

    /**
     * Prints the main menu and handles user input for 
     * main menu commands.
     */
    public static void mainMenu() {
        System.out.println("9. Get status of a user");
        System.out.println("8. Send today's date in a direct Slack channel");
        System.out.println("7. Set bot's presence to away");
        System.out.println("6. Set bot's presence to auto");
        System.out.println("5. Get presence of a user");
        System.out.println("4. Send a file to the Slack channel");
        System.out.println("3. Invite user into the Slack channel");
        System.out.println("2. Send today's date in the Slack channel");
        System.out.println("1. Fetch message history (last 10)");
        System.out.println("0. Exit\n");
        
        //Get user input
        try {
            int userInput = Integer.parseInt(inputOutput("Please press the number you want."));
            
            if (userInput >= 0 && userInput <=9) {
                if (userInput == 9) getUserStatus();
                if (userInput == 8) sendDirectMessageLocalDate();
                if (userInput == 7) setPresenceAway();
                if (userInput == 6) setPresenceAuto();
                if (userInput == 5) getPresence();
                if (userInput == 4) sendFile();
                if (userInput == 3) inviteUserToChannel();
                if (userInput == 2) sendMessageLocalDate();
                if (userInput == 1) fetchTenLastMessagesFromChannelHistory();
                if (userInput == 0) System.exit(0);
            } else {
                System.out.println("Please enter a number from 0 - 9");
                mainMenu();
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number from 0 - 9");
            mainMenu();
        }
    }

    /**
     * Fetch last ten messages and print them out
     */
    public static void fetchTenLastMessagesFromChannelHistory() {
        try {
            List<SlackMessagePosted> messages = FetchingMessageHistory.fetchTenLastMessagesFromChannelHistory(session, channel);

            for (SlackMessagePosted m : messages){
                System.out.println(m.getMessageContent());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            mainMenu();
        }
    }

    public static void sendMessageLocalDate()
    {
        String now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        session.sendMessage(channel, "Today's date is " + now);
        mainMenu();
    }

    public static void sendDirectMessageLocalDate()
    {
        String uid = inputOutput("Enter user ID to message");
        SlackUser user = session.findUserById(uid);
        String msg = "Today's date is " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        SlackChannel directChannel = SlackChannel.builder()
        .id(uid)
        .build();

        session.sendMessage(directChannel, msg);

        mainMenu();
    }

    public static void inviteUserToChannel()
    {
        String uid = inputOutput("Enter user ID to invite");
        SlackUser user = SlackPersonaImpl.builder().id(uid).build();

        session.inviteToChannel(channel, user);
        mainMenu();
    }

    public static void getPresence()
    {
        String uid = inputOutput("Enter user ID to get presence of");
        SlackPersona persona = SlackPersonaImpl.builder().id(uid).build();
        SlackPresence presence = session.getPresence(persona);
        System.out.println("Presence: " + presence.getPresence());
        mainMenu();
    }

    public static void setPresenceAway()
    {
        session.setPresence(SlackPresence.AWAY);
        mainMenu();
    }

    public static void setPresenceAuto()
    {
        session.setPresence(SlackPresence.AUTO);
        mainMenu();
    }

    public static void sendFile()
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String fileName = inputOutput("Enter full path of file to send");
        Path filePath = Paths.get(fileName);
        File fileToSend = new File(fileName);
        try {
            InputStream data = new FileInputStream(fileToSend);
            SlackMessageReply reply = session.sendFile(channel, data, filePath.getFileName().toString()).getReply();
            if(reply.isOk()) {
                System.out.println("***** Sendfile result: Ok");
            }
            else {
                System.out.println(reply.getErrorMessage());
            }
        }
        catch (FileNotFoundException e){
            System.out.println("File not found: " + fileName);
            mainMenu();
        }
        mainMenu();
    }

    public static void getUserStatus()
    {
        String uid = inputOutput("Enter user ID to message");
        SlackUser user = session.findUserById(uid);
        System.out.println(user.getStatusText());
        System.out.println(user.getStatusEmoji());
        mainMenu();
    }

    /**
     * Passes a prompt to the user and returns the user specified 
     * string.
     * @param message
     * @return String
     */
    private static String inputOutput(String message) {
        System.out.println(message);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String returnString = "";
        try {
            returnString = br.readLine();
        }
        catch (IOException e){
            System.out.println("Error reading in value");
            mainMenu();
        }
        return returnString;
    }
    
    /**
     * Performs slack api client setup and spins up cli menu
     * @param args
     */
    public static void main(String[] args) {
        String botToken = System.getenv("CS427GROUP20_SLACK_BOT_AUTHTOKEN").toString();
        String appToken = System.getenv("CS427GROUP20_SLACK_APP_AUTHTOKEN").toString();
        session = SlackSessionFactory.createWebSocketSlackSession(botToken, appToken);
        try{
            session.connect();
        }
        catch (IOException e){
            System.out.println("Error reading in value");
            mainMenu();
        }
        channel = session.findChannelByName("test-simple-slack-api");
        System.out.println("Welcome to group 20's fabulous simple-slack-api test interface!\n");
        mainMenu();
    }
}
