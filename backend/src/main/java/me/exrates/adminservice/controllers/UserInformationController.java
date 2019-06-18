package me.exrates.adminservice.controllers;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;
import me.exrates.adminservice.core.service.CoreUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = "/api/user-information", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserInformationController {

    private final CoreUserService coreUserService;

    @Autowired
    public UserInformationController(CoreUserService coreUserService) {
        this.coreUserService = coreUserService;
    }

    @PostMapping("/user-operation-types")
    public ResponseEntity editUserOperationTypeAuthorities(List<UserOperationAuthorityOption> options,
                                                           Integer userId) {
        coreUserService.updateUserOperationAuthority(options, userId);
        return ResponseEntity.ok().build();
    }
}