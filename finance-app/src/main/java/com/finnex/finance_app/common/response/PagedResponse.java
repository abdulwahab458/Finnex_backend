package com.finnex.finance_app.common.response;

import lombok.Data;
import org.springframework.data.domain.Page;
import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PagedResponse<T> of(Page<T> pageResult) {
        PagedResponse<T> r = new PagedResponse<>();
        r.content = pageResult.getContent();
        r.page = pageResult.getNumber();
        r.size = pageResult.getSize();
        r.totalElements = pageResult.getTotalElements();
        r.totalPages = pageResult.getTotalPages();
        r.last = pageResult.isLast();
        return r;
    }
}

