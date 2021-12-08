package org.example.cs427group20;

import com.google.gson.JsonArray;
import org.example.cs427group20.FetchingMessageHistory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.blocks.Block;
import com.ullink.slack.simpleslackapi.events.*;
import com.ullink.slack.simpleslackapi.events.userchange.SlackTeamJoin;
import com.ullink.slack.simpleslackapi.events.userchange.SlackUserChange;
import com.ullink.slack.simpleslackapi.impl.SlackPersonaImpl;
import com.ullink.slack.simpleslackapi.replies.*;
import com.ullink.slack.simpleslackapi.replies.GenericSlackReply;
//import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.SlackUser;


import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class MainTest {
    private static SlackSession session;
    private static SlackChannel channel;

    /**
     * Setup should run before any test.
     * Without fixes for #283 and #279, this will fail due to invalid auth.
     * If this fails, none of the other tests will work.
     */
    @BeforeAll
    public static void setup() {
        // Add your bot and app level token here!
        String botToken = "";
        String appToken = "";
        session = SlackSessionFactory.createWebSocketSlackSession(botToken, appToken);
        try{
            session.connect();
        }
        catch (IOException e){
            System.out.println("Error reading in value");
        }
        channel = session.findChannelByName("test-simple-slack-api");
    }

    /**
     * Gets the status for a predefined user to test issue #196
     */
    @Test
    public void testGetUserStatus()
    {
        // Add your user id here!
        String uid = "";
        SlackUser user = session.findUserById(uid);
        Assertions.assertTrue(user.getStatusText().equals("Working remotely"));
        Assertions.assertTrue(user.getStatusEmoji().equals(":house_with_garden:"));
    }

    /**
     * Invites a predetermined user to a channel to test issue #284
     */
    @Test
    public void testInviteUserToChannel()
    {
        // Add your user id here!
        String uid = "";
        SlackUser user = SlackPersonaImpl.builder().id(uid).build();
        session.inviteToChannel(channel, user);

        Collection<SlackUser> members = channel.getMembers();

        boolean didFindUser = false;
        for (SlackUser member : members) {
            if(member.getId().equals(uid)){
                didFindUser = true;
            }
        }

        Assertions.assertTrue(didFindUser);
    }

    /**
     * Sends a direct message to a predefined user to test issue #157
     */
    @Test
    public void testSendDirectMessage()
    {
        // Add your user id here!
        String uid = "";
        SlackUser user = session.findUserById(uid);
        String msg = "Today's date is " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        SlackChannel directChannelAsUid = SlackChannel.builder().id(uid).build();

        session.sendMessage(directChannelAsUid, msg);

        SlackMessageHandle<GenericSlackReply> reply = session.openDirectMessageChannel(user);

        String channelAnswer = reply.getReply().getPlainAnswer();

        JsonParser parser = new JsonParser();
        String channelId = parser.parse(channelAnswer).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
        SlackChannel directChannel = session.findChannelById(channelId);

        SlackMessagePosted messagePosted = FetchingMessageHistory.fetchOneRecentMessageFromChannelHistory(session, directChannel);

        Assertions.assertTrue(messagePosted.getMessageContent().equals(msg));
    }

}
