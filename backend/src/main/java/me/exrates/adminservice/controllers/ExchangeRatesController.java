package me.exrates.adminservice.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "/api/exchange-rates", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExchangeRatesController {

}