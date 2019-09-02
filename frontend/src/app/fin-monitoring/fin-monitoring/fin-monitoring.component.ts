import { Component, OnInit } from '@angular/core';
import { LoaderService } from 'src/app/shared/services/loader.service';
import { FinMonitoringService } from '../services/fin-monitoring.service';
import { takeUntil } from 'rxjs/operators';
import { Subject, forkJoin } from 'rxjs';



@Component({
  selector: 'app-fin-monitoring',
  templateUrl: './fin-monitoring.component.html',
  styleUrls: ['./fin-monitoring.component.scss']
})
export class FinMonitoringComponent implements OnInit {

  private ngUnsubscribe: Subject<void> = new Subject<void>();
  public offsetT1 = 0;
  public limitT1 = 20;
  public offsetT2 = 0;
  public limitT2 = 20;

  tabs = {
    SUM: 'sum',
    DIFF: 'diff',
  };

  currTab = this.tabs.SUM;

  constructor(
    public loaderService: LoaderService,
    private finMService: FinMonitoringService,
  ) { }

  ngOnInit() {
    this.getTab1();
  }

  toggleTab(tab: string) {
    this.currTab = tab;
    if (this.currTab === this.tabs.SUM) {
      this.offsetT1 = 0;
      this.limitT1 = 20;
      this.getTab1();
    } else {
      this.offsetT2 = 0;
      this.limitT2 = 20;
      this.getTab2();
    }

  }

  getTab1() {
    this.loaderService.toggleLoader(true);
    return forkJoin(
      this.finMService.getDashboardOne(),
      this.finMService.getExternalWallets({offset: this.offsetT1, limit: this.limitT1})
    )
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((res) => {
        console.log(res);
        this.loaderService.toggleLoader(false);
      }, (err) => {
        console.log(err);
        this.loaderService.toggleLoader(false);
      });
  }

  getTab2() {
    this.loaderService.toggleLoader(true);
    return forkJoin(
      this.finMService.getDashboardTwo(),
      this.finMService.getBalancesSlice({offset: this.offsetT2, limit: this.limitT2})
    )
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((res) => {
        console.log(res);
        this.loaderService.toggleLoader(false);
      }, (err) => {
        console.log(err);
        this.loaderService.toggleLoader(false);
      });
  }

  changePageT1(newOffSet) {
    this.offsetT1 = newOffSet;
  }

  changePageT2(newOffSet) {
    this.offsetT2 = newOffSet;
  }

}
