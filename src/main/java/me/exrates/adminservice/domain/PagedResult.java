package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedResult<T> {

    private int count;
    private List<T> items;

    public PagedResult() {
        items = new ArrayList<>();
    }
}