package com.igodating.chat.service.service;

import com.igodating.chat.service.exception.ChatMemberCreateException;
import com.igodating.chat.service.exception.UserNotAuthorizedException;
import com.igodating.chat.service.mapper.MessageMapper;
import com.igodating.chat.service.service.restrictions.ChatRestrictionsService;
import com.igodating.chat.service.service.restrictions.strategy.CheckResponse;
import com.igodating.boot.commons.util.ExceptionToStringConverter;
import com.igodating.boot.media.api.connector.MediaServiceConnector;
import com.igodating.boot.media.api.request.ImageSpecification;
import com.igodating.boot.media.api.response.SingleImageResponse;
import com.igodating.boot.user.api.connector.UserServiceConnector;
import com.igodating.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.igodating.boot.user.api.global.security.jwt.JwtUserWrapper;
import com.igodating.boot.user.api.response.InfoByProfileId;
import com.igodating.chat.api.ChatType;
import com.igodating.chat.api.request.ChatCreateRequest;
import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.api.request.GetChatRequest;
import com.igodating.chat.api.request.GetChatSummaryRequest;
import com.igodating.chat.api.request.GetChatsByPageRequest;
import com.igodating.chat.api.request.GetMessagesByPageRequest;
import com.igodating.chat.api.response.ChatInfoByUserShared;
import com.igodating.chat.api.response.ChatPaginationShared;
import com.igodating.chat.api.response.ChatShared;
import com.igodating.chat.api.response.ChatSummaryResponse;
import com.igodating.chat.api.response.CheckRestrictionsResponseShared;
import com.igodating.chat.api.response.MessageShared;
import com.igodating.chat.db.api.ChatRepository;
import com.igodating.chat.db.api.FileOperation;
import com.igodating.chat.db.api.dto.ChatDto;
import com.igodating.chat.db.api.dto.ChatInfoByUserDto;
import com.igodating.chat.db.api.dto.ChatMemberCreateDto;
import com.igodating.chat.db.api.dto.ChatPaginationDto;
import com.igodating.chat.db.api.dto.MessageDto;
import com.igodating.commons.ActionResult;
import com.igodating.commons.exception.ApiErrorCode;
import com.igodating.commons.exception.FailedActionResultException;
import com.igodating.commons.model.FileAttachment;
import com.igodating.commons.model.image.ImageExtension;
import com.igodating.commons.model.image.ImageSize;
import com.igodating.commons.model.image.SimpleImage;
import com.igodating.commons.model.page.Page;
import com.igodating.commons.util.MimeTypesUtil;
import com.j256.simplemagic.ContentType;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class ChatService {

    SimpMessagingTemplate webSocketMessagingTemplate;
    ChatRepository chatPostgresRepository;
    MediaServiceConnector mediaServiceConnector;
    UserServiceConnector userServiceConnector;
    MessageMapper messageMapper;
    ChatCache chatCache;
    ExceptionToStringConverter exceptionToStringConverter;
    @NonFinal
    Set<String> allowedFileExtensions;
    int messageLimit;
    ChatRestrictionsService chatRestrictionsService;


    public ChatService(SimpMessagingTemplate webSocketMessagingTemplate,
                       ChatRepository chatPostgresRepository,
                       MediaServiceConnector mediaServiceConnector,
                       UserServiceConnector userServiceConnector,
                       MessageMapper messageMapper,
                       ChatCache chatCache,
                       ExceptionToStringConverter exceptionToStringConverter,
                       @Value("${default.message-limit:50}") int messageLimit,
                       ChatRestrictionsService chatRestrictionsService) {
        this.webSocketMessagingTemplate = webSocketMessagingTemplate;
        this.chatPostgresRepository = chatPostgresRepository;
        this.mediaServiceConnector = mediaServiceConnector;
        this.userServiceConnector = userServiceConnector;
        this.messageMapper = messageMapper;
        this.chatCache = chatCache;
        this.exceptionToStringConverter = exceptionToStringConverter;
        this.messageLimit = messageLimit;
        this.chatRestrictionsService = chatRestrictionsService;
    }

    @PostConstruct
    public void init() {
        this.allowedFileExtensions = this.chatPostgresRepository.getAllowedFilesExtensions(FileOperation.CHAT);
    }

    @Transactional
    public Long createChat(ChatCreateRequest request) {
        final Long chatId = this.chatPostgresRepository.createChat(request);

        final Set<ChatMemberCreateDto> memberCreateDtoList = this.chatPostgresRepository.getMandatoryMembers(request.getType())
                .stream()
                .filter(profileId2Type -> !(profileId2Type.getKey().equals(request.getCreatorProfileId()) && profileId2Type.getValue() == request.getCreatorProfileType()))
                .map(x -> {
                    final InfoByProfileId mandatoryMember = this.userServiceConnector.getInfoByProfileId(x.getLeft(), x.getRight().isLegal()).orElseThrow();
                    return ChatMemberCreateDto.builder()
                            .userName(mandatoryMember.getUserContact().getFullName().toString())
                            .profileType(mandatoryMember.getProfileType())
                            .profileId(x.getLeft())
                            .build();
                })
                .collect(Collectors.toSet());

        final InfoByProfileId creatorInfo = this.userServiceConnector.getInfoByProfileId(request.getCreatorProfileId(), request.getCreatorProfileType().isLegal()).orElseThrow();

        request.getOpponents().forEach(opponent -> {
            final InfoByProfileId opponentInfo = this.userServiceConnector.getInfoByProfileId(opponent.getProfileId(), opponent.isLegal()).orElseThrow();
            memberCreateDtoList.add(ChatMemberCreateDto.builder()
                    .userName(opponentInfo.getUserContact().getFullName().toString())
                    .profileType(opponentInfo.getProfileType())
                    .profileId(opponent.getProfileId())
                    .build());
        });

        final Long chatCreatorId = this.chatPostgresRepository.addChatMember(chatId, new ChatMemberCreateDto(request.getCreatorProfileId(), creatorInfo.getProfileType(), creatorInfo.getUserContact().getFullName().toString()));
        if (chatCreatorId != null) {
            this.chatPostgresRepository.updateCreator(chatId, chatCreatorId);
        } else {
            log.error("Member with id:{} not added to chat", request.getCreatorProfileId());
            throw new ChatMemberCreateException("Member not added");
        }
        this.chatPostgresRepository.addChatMembers(chatId, memberCreateDtoList);
        return chatId;
    }

    @Transactional
    @PreAuthorize("@securityExpressions.checkUserHasAccessToChat(#chatId, principal)")
    public MessageShared createMessage(long chatId, CreateMessageRequest request, JwtUserWrapper principal) {
        final CheckResponse checkResponse = chatRestrictionsService.check(chatId, request);
        if (checkResponse.isAllCheckPassed()) {
            final MessageDto message = this.chatPostgresRepository.createMessage(chatId, request);
            final MessageShared messageShared = this.messageMapper.mapMessageToShared(message);
            messageShared.setGuid(request.getGuid());
            this.webSocketMessagingTemplate.convertAndSend(ChatWsRoutes.CREATE_MESSAGE_EVENT.replace("{chat_id}", String.valueOf(chatId)), messageShared);
            return messageShared;
        } else {
            this.webSocketMessagingTemplate.convertAndSend(ChatWsRoutes.CHECK_RESTRICTION_MESSAGE_EVENT.replace("{chat_id}", String.valueOf(chatId)),
                    CheckRestrictionsResponseShared.builder()
                            .messages(checkResponse.getMessages())
                            .guid(request.getGuid())
                            .build());
            log.error("Check restrictions is failed. Messages - {}", checkResponse.getMessages());
            return null;
        }
    }

    @PreAuthorize("@securityExpressions.checkUserHasAccessToChat(#chatId, principal)")
    public ChatShared getById(long chatId, GetChatRequest request) {
        final ChatDto chat = this.chatPostgresRepository.getById(chatId, request, this.messageLimit);
        return this.messageMapper.mapChatToShared(chat);
    }

    public Page<ChatPaginationShared> getByPage(GetChatsByPageRequest request) {
        final Page<ChatPaginationDto> chatPage = this.chatPostgresRepository.getByPage(request);
        return chatPage.convertPage(this.messageMapper::mapChatToPaginationShared);
    }

    @PreAuthorize("@securityExpressions.checkUserHasAccessToChat(#request.getChatId(), principal)")
    public Page<MessageShared> getMessagesByPage(GetMessagesByPageRequest request) {
        return this.chatPostgresRepository.getMessagesByPage(request)
                .convertPage(this.messageMapper::mapMessageToShared);
    }

    @Transactional
    @PreAuthorize("@securityExpressions.checkUserHasAccessToChat(#chatId, principal)")
    //TODO not working. Need refactor - we should added member with concrete profile_type
    public boolean addChatMember(long chatId, Collection<Long> profileIds) {
        //final boolean success = this.chatPostgresRepository.addChatMembers(chatId, memberIds);
        //final InfoByProfileId buyerInfo = this.userServiceConnector.getInfoByProfileId(request.getCreatorProfileId()).orElseThrow();
        final boolean success = false;
        if (success) {
            final List<String> userNames = userServiceConnector.getUserContactData(new ArrayList<>(profileIds)).orElseThrow()
                    .stream()
                    .map(x -> x.getFullName().toString())
                    .toList();
            chatCache.invalidate(chatId);
            webSocketMessagingTemplate.convertAndSend(ChatWsRoutes.ADD_MEMBER_TO_CHAT, userNames);
        }
        return success;
    }

    public Set<Long> getMembersByChatId(long chatId) {
        return this.chatPostgresRepository.getMembersByChatId(chatId);
    }

    @Transactional
    public boolean leaveChat(long chatId, JwtSecurityUser principal) {
        final boolean success = this.chatPostgresRepository.leaveChat(chatId, principal.getId());
        if (success) {
            chatCache.invalidate(chatId);
            webSocketMessagingTemplate.convertAndSend(ChatWsRoutes.LEAVE_CHAT, List.of(principal.getFullName().toString()));
        }
        return success;
    }

    public ChatSummaryResponse getChatSummaryByUser(GetChatSummaryRequest request) {
        final Map<Long, ChatInfoByUserShared> chatId2ChatInfo = this.chatPostgresRepository.getChatSummaryByUser(request.getProfileId())
                .stream().collect(Collectors.toMap(ChatInfoByUserDto::getId, this.messageMapper::mapToMetaDataShared));
        final Map<ChatType, Long> type2Count = this.chatPostgresRepository.getAllUnreadMessageCountGroupedByChatType(request.getProfileId());
        return new ChatSummaryResponse(chatId2ChatInfo, type2Count);
    }

    @PreAuthorize("@securityExpressions.checkUserHasAccessToChat(#chatId, principal)")
    @Transactional
    public boolean readMessage(JwtUserWrapper principal, long chatId, List<Long> messageIds) {
        final JwtSecurityUser jwtSecurityUser = this.extractJwtUser(principal);
        return this.chatPostgresRepository.readMessages(chatId, jwtSecurityUser.getCurrentProfileReferenceId(), messageIds);
    }

    @Transactional
    public FileAttachment uploadAttachment(MultipartFile file) {
        if (!this.allowFileExtension(file)) {
            throw new FailedActionResultException(ActionResult.fail(ApiErrorCode.FORBIDDEN_FILE_EXTENSION));
        }
        return this.mediaServiceConnector.attachmentUpload(file).orElseThrow();
    }

    @Transactional
    @PreAuthorize("@securityExpressions.checkUserHasAccessToChat(#chatId, principal)")
    public boolean uploadChatAvatar(long chatId, MultipartFile image) {
        final SimpleImage simpleImage = this.uploadImage(chatId, "CHAT_AVATAR", image);
        return this.chatPostgresRepository.uploadChatAvatar(chatId, simpleImage);
    }

    public boolean allowFileExtension(MultipartFile file) {
        try {
            final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!allowedFileExtensions.contains(extension)) {
                return false;
            }
            final Optional<ContentType> contentType = MimeTypesUtil.getContentType(file.getBytes());
            return contentType.filter(x -> !Collections.disjoint(allowedFileExtensions, Arrays.asList(x.getFileExtensions()))).isPresent();

        } catch (IOException e) {
            log.error(e);
            throw new FailedActionResultException(ActionResult.fail(ApiErrorCode.INTERNAL_SERVER_ERROR,
                    "File " + file.getName() + "couldn't be saved to storage: " + this.exceptionToStringConverter.convert(e)
            ));
        }
    }

    private SimpleImage uploadImage(long chatId, String entityType, MultipartFile image) {
        final SingleImageResponse singleImage = this.mediaServiceConnector.imageUpload(image, entityType, String.valueOf(chatId))
                .orElseThrow();
        final String path = singleImage.getPath();
        final EnumMap<ImageSize, EnumSet<ImageExtension>> sizes = new EnumMap<>(ImageSize.class);
        for (ImageSpecification specification : singleImage.getSpecifications()) {
            sizes.put(specification.getImageSize(), specification.getExtensions());
        }
        return new SimpleImage(path, sizes);
    }

    private JwtSecurityUser extractJwtUser(JwtUserWrapper userWrapper) {
        if (userWrapper != null) {
            return (userWrapper instanceof JwtSecurityUser jwtSecurityUser) ? jwtSecurityUser : userWrapper.getJwtUser();
        } else throw new UserNotAuthorizedException("User was not authorized");
    }
}
