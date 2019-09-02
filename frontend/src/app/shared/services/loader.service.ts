import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class LoaderService {
  private loader$ = new BehaviorSubject<boolean>(false);

  getLoader() {
    return this.loader$;
  }

  toggleLoader(val: boolean): void {
    setTimeout(() => {
      this.loader$.next(val);
    }, 0);
  }

  getLoadingValue(args: boolean[]) {
    return args.some((a) => a);
  }
}
