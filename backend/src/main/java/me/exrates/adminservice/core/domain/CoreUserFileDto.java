package me.exrates.adminservice.core.domain;

import lombok.Data;

import java.nio.file.Path;

@Data
public class CoreUserFileDto {

    private int id;
    private int userId;
    private Path path;
}