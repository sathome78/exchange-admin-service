package me.exrates.adminservice.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/wallet-balances")
public class WalletBalancesController {

//    @PostMapping(value = "/address/delete")
//    public ResponseEntity deleteWalletAddress(@RequestParam int id,
//                                              @RequestParam int currencyId,
//                                              @RequestParam String walletAddress) {
//        walletService.deleteWalletAddress(id, currencyId, walletAddress);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
