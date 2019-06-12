import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FinMonitoringService {

  constructor(private http: HttpClient) { }

  baseUrl: string = 'http://localhost:7777/api/financial-monitoring/';

  // Reserved Wallets

  createReservedWallet(wallet) {
    return this.http.post(`${this.baseUrl}reserved-wallets?currencyId=${wallet.currencyId}`, wallet);
  }

  deleteReservedWallet(wallet) {
    return this.http.delete(`${this.baseUrl}reserved-wallets?id=${wallet.id}
    &currencyId=${wallet.currencyId}&walletAddress=${wallet.walletAddress}'`);
  }

  saveAsAddressReservedWallet(wallet) {
    return this.http.put(`${this.baseUrl}reserved-wallets/as-address?id=${wallet.id}
    &currencyId=${wallet.currencyId}&walletAddres=${wallet.walletAddress}`, wallet);
  }

  saveAsNameReservedWallet(wallet) {
    let url = `${this.baseUrl}reserved-wallets/as-name?id=${wallet.id}
    &currencyId=${wallet.currencyId}&walletAddres=${wallet.walletAddress}
    &reservedWalletBalance=${wallet.balance}`;
    if (wallet.name) {
      url += `&name=${wallet.name}`;
    }
    return this.http.put(url, {});
  }

  getReservedWallets(value) {
    return this.http.get(`${this.baseUrl}reserved-wallets/${value.currencyId}`);
  }

  saveAccountingImbalance(value) {
    return this.http.put(`${this.baseUrl}accounting-imbalance?currencyName=${value.currencyName}&
    accountingProfit=${value.accountingProfit}&accountingManualBalanceChanges=${value.accountingManualBalanceChanges}`, {});
  }

}
