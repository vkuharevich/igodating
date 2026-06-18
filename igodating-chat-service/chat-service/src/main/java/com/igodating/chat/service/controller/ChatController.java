package com.igodating.chat.service.controller;

import com.igodating.chat.service.service.ChatService;
import com.igodating.chat.service.service.ChatWsRoutes;
import com.igodating.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.igodating.boot.user.api.global.security.jwt.JwtUserWrapper;
import com.igodating.chat.api.ChatRoutes;
import com.igodating.chat.api.request.*;
import com.igodating.chat.api.response.ChatPaginationShared;
import com.igodating.chat.api.response.ChatShared;
import com.igodating.chat.api.response.ChatSummaryResponse;
import com.igodating.chat.api.response.MessageShared;
import com.igodating.commons.ActionResult;
import com.igodating.commons.model.FileAttachment;
import com.igodating.commons.model.page.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(description = "Chat service API", name = "Chat service")
public class ChatController {
    ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Create chat", description = "Create chat by request")
    @PostMapping(ChatRoutes.External.CHATS)
    public ActionResult<Long> createChat(@RequestBody @Validated ChatCreateRequest request) {
        return ActionResult.ok(this.chatService.createChat(request));
    }

    @Operation(summary = "get chat by page", description = "get chat by page")
    @GetMapping(ChatRoutes.External.CHATS)
    public ActionResult<Page<ChatPaginationShared>> getByPage(@ParameterObject @Validated GetChatsByPageRequest request) {
        return ActionResult.ok(this.chatService.getByPage(request));
    }

    @Operation(summary = "get chat by id", description = "get chat by id")
    @GetMapping(ChatRoutes.External.CHAT_BY_ID)
    public ActionResult<ChatShared> getById(@PathVariable("id") long chatId, @ParameterObject @Validated GetChatRequest request) {
        return ActionResult.ok(this.chatService.getById(chatId, request));
    }

    @Operation(summary = "leave chat", description = "Leave chat. User should be authorized")
    @DeleteMapping(ChatRoutes.External.LEAVE_CHAT)
    public ActionResult<Boolean> leaveChat(@AuthenticationPrincipal JwtSecurityUser principal,
                                           @PathVariable("id") long chatId) {
        return ActionResult.ok(this.chatService.leaveChat(chatId, principal));
    }

    @Operation(summary = "Add chat member", description = "Add chat member to chat by id")
    @PostMapping(ChatRoutes.External.CHAT_MEMBERS)
    public ActionResult<Boolean> addChatMember(@PathVariable("id") long chatId, @RequestBody List<Long> memberIds) {
        return ActionResult.ok(this.chatService.addChatMember(chatId, memberIds));
    }

    @MessageMapping(ChatWsRoutes.CREATE_MESSAGE)
    public ActionResult<MessageShared> createMessage(@DestinationVariable("chat_id") @Min(1) long chatId,
                                                     @Payload @Validated CreateMessageRequest request,
                                                     @AuthenticationPrincipal JwtUserWrapper principal) {
        return ActionResult.ok(this.chatService.createMessage(chatId, request, principal));
    }

    @Operation(summary = "Get messages", description = "Get messages by page")
    @GetMapping(ChatRoutes.External.CHAT_MESSAGES)
    public ActionResult<Page<MessageShared>> getMessagesByPage(@ParameterObject @Validated GetMessagesByPageRequest request) {
        return ActionResult.ok(this.chatService.getMessagesByPage(request));
    }

    @MessageMapping(ChatWsRoutes.READ_MESSAGES)
    public ActionResult<Boolean> readMessages(@DestinationVariable("chat_id") @Min(1) long chatId,
                                              @AuthenticationPrincipal JwtUserWrapper principal,
                                              @Payload List<Long> messageIds) {
        return ActionResult.ok(this.chatService.readMessage(principal, chatId, messageIds));
    }

    @Operation(summary = "Get summary by chats",
            description = """
                    Get summary response by chat which contains info about
                    unread messages and meta info about chat. Response is map where key is chat id""")
    @GetMapping(ChatRoutes.External.CHAT_SUMMARY)
    public ActionResult<ChatSummaryResponse> chatSummaryByUser(@ParameterObject @Validated GetChatSummaryRequest request) {
        return ActionResult.ok(this.chatService.getChatSummaryByUser(request));
    }

    @Operation(summary = "Upload attachment to chat")
    @PostMapping(value = ChatRoutes.External.CHAT_ATTACHMENTS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult<FileAttachment> uploadAttachment(@Parameter(description = "Attachment")
                                                         @RequestPart("file") MultipartFile file) {
        return ActionResult.ok(this.chatService.uploadAttachment(file));
    }

    @Operation(summary = "Upload avatar of chat")
    @PatchMapping(value = ChatRoutes.External.CHAT_AVATAR, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult<Boolean> uploadAvatar(@PathVariable("id") long chatId,
                                              @Parameter(description = "image for avatar")
                                              @RequestPart(value = "image") MultipartFile file) {
        return ActionResult.ok(this.chatService.uploadChatAvatar(chatId, file));
    }
}
