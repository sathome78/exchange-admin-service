import { Component, OnInit } from '@angular/core';
import { PopupService } from 'src/app/shared/services/popup.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-monitoring-popup',
  templateUrl: './monitoring-popup.component.html',
  styleUrls: ['./monitoring-popup.component.scss']
})
export class MonitoringPopupComponent implements OnInit {

  form: FormGroup;

  constructor(
    private popupService: PopupService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.form = this.formBuilder.group({
      coin: ['', Validators.pattern('^[0-9,]*$')],
      usd: ['', Validators.pattern('^[0-9,]*$')]
    })
  }

  emptyField(field) {
    this.form.value[field] = '';
  }

  onChange() {
    if (this.form.valid && (this.form.value.coin || this.form.value.usd)) {
      this.popupService.toggleMonitoringPopup(false);
    }
  }

  close() {
    this.popupService.toggleMonitoringPopup(false);
  }

}
