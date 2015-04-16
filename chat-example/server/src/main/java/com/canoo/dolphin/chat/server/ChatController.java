package com.canoo.dolphin.chat.server;

import com.canoo.dolphin.chat.model.Chat;
import com.canoo.dolphin.chat.model.ChatMessage;
import com.canoo.dolphin.chat.model.ChatUser;
import com.canoo.dolphin.chat.model.State;
import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.EventHandler;
import com.canoo.dolphin.server.event.HandlerIdentifier;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@DolphinController("chat")
public class ChatController {

    @Inject
    private BeanManager manager;

    @Inject
    private DolphinEventBus eventBus;

    private Map<Long, HandlerIdentifier> eventHandlerIdentifiers = new HashMap<>();

    @DolphinAction
    public void init(@Param Long chatId) {
        Chat chat = manager.create(Chat.class);
        chat.getEntityIdProperty().set(chatId);

        HandlerIdentifier handlerIdentifier = eventBus.registerHandler("chat." + chatId, v -> System.out.println(""));
        eventHandlerIdentifiers.put(chatId, handlerIdentifier);
    }


    @DolphinAction
    public void close(@Param Long chatId) {
        Chat chat = manager.createQuery(Chat.class).withEquals("entityId", chatId).getSingle();

        HandlerIdentifier handlerIdentifier = eventHandlerIdentifiers.remove(chatId);
        eventBus.unregisterHandler(handlerIdentifier);

        manager.delete(chat);
    }

    @DolphinAction
    public void sendMessage(@Param Long chatId, @Param String message) {
        Chat chat = manager.createQuery(Chat.class).withEquals("entityId", chatId).getSingle();

        ChatUser me = manager.create(ChatUser.class);
        me.getNameProperty().set("Me");
        me.getEntityIdProperty().set(1234L);
        me.getStateProperty().set(State.ONLINE);

        ChatMessage newMessage = manager.create(ChatMessage.class);
        newMessage.getTimeStampProperty().set(System.nanoTime());
        newMessage.getMessageProperty().set(message);
        newMessage.getSenderProperty().set(me);

        chat.getMessages().add(newMessage);

        eventBus.publish("chat." + chat.getEntityId(), newMessage);
    }

}
