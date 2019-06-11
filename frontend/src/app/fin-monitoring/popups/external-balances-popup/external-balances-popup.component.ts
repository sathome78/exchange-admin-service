import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { PopupService } from 'src/app/services/popup.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { FinMonitoringService } from '../../services/fin-monitoring.service';

@Component({
  selector: 'app-external-balances-popup',
  templateUrl: './external-balances-popup.component.html',
  styleUrls: ['./external-balances-popup.component.scss']
})
export class ExternalBalancesPopupComponent implements OnInit, OnDestroy {
  ngUnsubscribe: Subject<void> = new Subject<void>();

  @Input() item = {
    id: 1,
    currencyId: 1,
    currencyName: 'USD'
  };

  balanceForm: FormGroup;
  walletForm: FormGroup;
  isAddWalletMode: boolean;
  reservedWallets = [{
    id: 1,
    currencyId: 1,
    walletAddress: 'qrWrerdgfdsgff',
    name: 'Балансы всех резервных кошельков 1',
    balance: '0'
  },
  {
    id: 1,
    currencyId: 1,
    walletAddress: 'qrWrerdgfdsgff',
    name: 'Балансы всех резервных кошельков 2',
    balance: '0'
  }];

  constructor(
    private popupService: PopupService,
    private finMonitoringService: FinMonitoringService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.initForms();
    this.getReservedWallets();
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  initForms() {
    this.balanceForm = this.formBuilder.group({
      profit: ['0', Validators.pattern('^[0-9,]*$')],
      balanceChange: ['0', Validators.pattern('^[0-9,]*$')]
    });
    this.walletForm = this.formBuilder.group({
      walletAddress: ['', Validators.required],
      name: [],
      reservedWalletBalance: ['0', Validators.pattern('^[0-9,]*$')]
    });
    this.balanceForm.valueChanges
    .subscribe(changes => {
        const value = {
          currencyName: this.item.currencyName,
          accountingProfit: changes.profit,
          accountingManualBalanceChanges: changes.balanceChange
          };
        this.finMonitoringService.saveAccountingImbalance(value)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(res => console.log(res));
    });
  }

  getReservedWallets() {
    // this.finMonitoringService.getReservedWallets(this.item)
    // .pipe(takeUntil(this.ngUnsubscribe))
    // .subscribe(response => {
    //   this.reservedWallets = response;
    // });
  }

  numbersOnlyValidation(event) {
    const inp = String.fromCharCode(event.keyCode);
    if (!/^\d+$/.test(inp)) {
      event.preventDefault();
    }
  }

  onAddResevedWallet() {
    this.isAddWalletMode = !this.isAddWalletMode;
  }

  saveReservedWallet(type: string) {
    const wallet = {
      ...this.item,
      ...this.walletForm.value
    };
    if (this.walletForm.valid && type === 'address') {
      this.finMonitoringService.saveAsAddressReservedWallet(wallet)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(_ => this.close());
    } else if (this.walletForm.valid && type === 'value') {
      this.finMonitoringService.saveAsNameReservedWallet(wallet)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(_ => this.close());
    }
  }

  removeWallet(removedWallet) {
    const wallet = {
      ...this.item,
      ...removedWallet
    };
    this.finMonitoringService.deleteReservedWallet(wallet)
    .pipe(takeUntil(this.ngUnsubscribe))
    .subscribe(res => console.log(res));
  }

  close() {
    this.popupService.toggleExternalBalancesPopup(false);
  }
}
