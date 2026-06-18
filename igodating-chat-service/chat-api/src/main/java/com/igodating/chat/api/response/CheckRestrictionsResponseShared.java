package com.igodating.chat.api.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CheckRestrictionsResponseShared(List<String> messages, String guid) {
}
