package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedResult<T> {

    private int count;
    private boolean hasNextPage;
    private List<T> items;

    public PagedResult() {
        items = new ArrayList<>();
    }

    public PagedResult(boolean hasNextPage, Collection<T> items) {
        this.hasNextPage = hasNextPage;
        this.items = new ArrayList<>(items);
    }
}
