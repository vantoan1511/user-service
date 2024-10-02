package com.shopbee.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private Long totalItems;
    private Long numberOfItems;
    private Integer page;
    private Long size;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> items;

    public PagedResponse(List<T> items) {
        this((long) items.size(), (long) items.size(), 1, Long.MAX_VALUE, false, false, items);
    }

    public PagedResponse(Long totalItems, Long numberOfItems, Integer page, Long size, List<T> items) {
        this(totalItems, numberOfItems, page, size, page * size < totalItems, page > 1, items);
    }

    public static <T> PagedResponse<T> from(List<T> users, PageRequest pageRequest) {
        List<T> pagedItems = users.stream()
                .skip((pageRequest.getPage() - 1) * pageRequest.getSize())
                .limit(pageRequest.getSize())
                .toList();
        return new PagedResponse<>(
                (long) users.size(), (long) pagedItems.size(), pageRequest.getPage(), pageRequest.getSize(), pagedItems);
    }

}
