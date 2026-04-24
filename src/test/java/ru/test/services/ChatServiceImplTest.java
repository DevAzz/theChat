package ru.test.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.test.entities.ChatUser;
import ru.test.sockets.ChatWebSocket;
import ru.test.utils.MessageType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {

    private ChatServiceImpl chatService;
    private ChatWebSocket mockWebSocket1;
    private ChatWebSocket mockWebSocket2;

    @BeforeEach
    void setUp() {
        chatService = new ChatServiceImpl();
        mockWebSocket1 = mock(ChatWebSocket.class);
        mockWebSocket2 = mock(ChatWebSocket.class);
    }

    @Test
    void testAddAndRemoveWebSocket() {
        chatService.add(mockWebSocket1);
        // We can't directly inspect the private set, but we can verify behavior via sendMessage
        chatService.sendMessage("test");
        verify(mockWebSocket1).sendString("test");

        chatService.remove(mockWebSocket1);
        chatService.sendMessage("test2");
        verify(mockWebSocket1, times(0)).sendString("test2"); // Should not be called again
    }

    @Test
    void testSendMessageToAllWebSockets() {
        chatService.add(mockWebSocket1);
        chatService.add(mockWebSocket2);

        String message = "Hello everyone!";
        chatService.sendMessage(message);

        verify(mockWebSocket1).sendString(message);
        verify(mockWebSocket2).sendString(message);
    }

    @Test
    void testSendMessageWithException() {
        chatService.add(mockWebSocket1);
        chatService.add(mockWebSocket2);

        doThrow(new RuntimeException("Connection error")).when(mockWebSocket1).sendString(anyString());

        // This should not throw an exception to the caller, as it's caught in ChatServiceImpl
        assertDoesNotThrow(() -> chatService.sendMessage("test"));
        
        // The second websocket should still receive the message
        verify(mockWebSocket1).sendString("test");
        verify(mockWebSocket2).sendString("test");
    }

    @Test
    void testSendChangeUserStatusMessage() {
        chatService.add(mockWebSocket1);
        
        ru.test.entities.api.IUser mockBaseUser = mock(ru.test.entities.api.IUser.class);
        when(mockBaseUser.getId()).thenReturn(1L);
        when(mockBaseUser.getLogin()).thenReturn("TestUser");

        ChatUser user = new ChatUser(mockBaseUser, true);

        chatService.sendChangeUserStatusMessage(user);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockWebSocket1).sendString(captor.capture());

        String sentMessage = captor.getValue();
        assertTrue(sentMessage.startsWith(MessageType.USER_LIST.getName() + "["));
        assertTrue(sentMessage.contains("TestUser"));
    }

    @Test
    void testSendChangeUsersStatusMessage() {
        chatService.add(mockWebSocket1);

        ru.test.entities.api.IUser mockBaseUser1 = mock(ru.test.entities.api.IUser.class);
        when(mockBaseUser1.getId()).thenReturn(1L);
        when(mockBaseUser1.getLogin()).thenReturn("User1");

        ru.test.entities.api.IUser mockBaseUser2 = mock(ru.test.entities.api.IUser.class);
        when(mockBaseUser2.getId()).thenReturn(2L);
        when(mockBaseUser2.getLogin()).thenReturn("User2");

        ChatUser user1 = new ChatUser(mockBaseUser1, true);
        ChatUser user2 = new ChatUser(mockBaseUser2, false);
        List<ChatUser> users = Arrays.asList(user1, user2);

        chatService.sendChangeUsersStatusMessage(users);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockWebSocket1).sendString(captor.capture());

        String sentMessage = captor.getValue();
        assertTrue(sentMessage.startsWith(MessageType.USER_LIST.getName() + "["));
        assertTrue(sentMessage.contains("User1"));
        assertTrue(sentMessage.contains("User2"));
    }
}