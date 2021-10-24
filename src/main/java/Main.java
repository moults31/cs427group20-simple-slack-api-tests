package org.example.cs427group20;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import org.example.cs427group20.FetchingMessageHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
        System.out.println("1. Fetch message history (last 10)");
        System.out.println("0. Exit\n");
        
        //Get user input
        try {
            int userInput = Integer.parseInt(inputOutput("Please press the number you want."));
            
            if (userInput >= 0 && userInput <=1) {
                if (userInput == 1) fetchTenLastMessagesFromChannelHistory();
                if (userInput == 0) System.exit(0);
            } else {
                System.out.println("Please enter a number from 0 - 6");
                mainMenu();
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number from 0 - 6");
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
        String userToken = System.getenv("CS427GROUP20_SLACK_USER_AUTHTOKEN").toString();
        session = SlackSessionFactory.createWebSocketSlackSession(userToken, botToken);
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
