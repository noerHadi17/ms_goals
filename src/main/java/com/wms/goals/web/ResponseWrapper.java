package com.wms.goals.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseWrapper<T> {
    private boolean success;
    private T data;
    private List<String> messages;
    private List<String> messageCodes;
}
