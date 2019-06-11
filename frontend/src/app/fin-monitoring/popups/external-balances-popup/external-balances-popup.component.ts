import { Component, OnInit, Input } from '@angular/core';
import { PopupService } from 'src/app/services/popup.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-external-balances-popup',
  templateUrl: './external-balances-popup.component.html',
  styleUrls: ['./external-balances-popup.component.scss']
})
export class ExternalBalancesPopupComponent implements OnInit {

  @Input() item = {
    coin: 'USD'
  };

  form: FormGroup;
  isAddWalletMode: boolean;

  constructor(
    private popupService: PopupService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.form = this.formBuilder.group({
      profit: ['0.1110945', Validators.pattern('^[0-9,]*$')],
      balanceChange: ['0.1110945', Validators.pattern('^[0-9,]*$')]
    });
  }

  onAddResevedWallet() {
    this.isAddWalletMode = !this.isAddWalletMode;
  }

  close() {
    this.popupService.toggleExternalBalancesPopup(false);
  }
}
