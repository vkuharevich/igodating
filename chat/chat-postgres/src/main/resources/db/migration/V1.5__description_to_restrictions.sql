comment on column chat_restrictions.type is 'Type of restriction. See ChatRestrictionType.java';

delete from chat_restrictions;

insert into chat_restrictions (type, duration, "limit", description)
VALUES ('LIMIT_CHAT_MESSAGES', interval '1 minute', 30, 'Limit count message in chats. If duration is null then limit applies to messages from all time in chat.'),
       ('LIMIT_SIZE_MESSAGE_ATTACHMENTS', null, 10, 'Limit size attachments in chats (MB). Duration is not involved in checking limit.'),
       ('LIMIT_CHAT_ATTACHMENTS', interval '1 minute', 2, 'Limit count attachment in chats. If duration is null then limit applies to attachments from all time in chat.');