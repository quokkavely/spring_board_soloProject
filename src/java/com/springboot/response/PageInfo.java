package com.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class PageInfo {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
