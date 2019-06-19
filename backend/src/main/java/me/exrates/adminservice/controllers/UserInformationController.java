package me.exrates.adminservice.controllers;

import com.google.common.io.ByteSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;
import me.exrates.adminservice.core.service.CoreUserService;
import me.exrates.adminservice.domain.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
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

    @GetMapping("/all")
    public ResponseEntity<PagedResult<UserInfoDto>> getAllUsersInfo(@RequestBody @Valid FilterDto filter,
                                                                    @RequestParam(required = false, defaultValue = "20") Integer limit,
                                                                    @RequestParam(required = false, defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(coreUserService.getAllUsersInfoFromCache(filter, limit, offset));
    }

    @GetMapping("/all/report")
    public ResponseEntity getAllUsersInfoReport(@RequestBody @Valid FilterDto filter,
                                                @RequestParam(required = false, defaultValue = "20") Integer limit,
                                                @RequestParam(required = false, defaultValue = "0") Integer offset) {
        ReportDto reportDto;
        try {
            reportDto = coreUserService.getAllUsersInfoReport(filter, limit, offset);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted", ex);
            return ResponseEntity.noContent().build();
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted", ex);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/dashboard-one")
    public ResponseEntity<UserDashboardDto> getDashboardOne() {
        return ResponseEntity.ok(coreUserService.getDashboardOne());
    }

    @PostMapping("/user-operation-types")
    public ResponseEntity editUserOperationTypeAuthorities(List<UserOperationAuthorityOption> options,
                                                           Integer userId) {
        coreUserService.updateUserOperationAuthority(options, userId);
        return ResponseEntity.ok().build();
    }
}