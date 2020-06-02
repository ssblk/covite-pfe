import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IVoiture, Voiture } from 'app/shared/model/voiture.model';
import { VoitureService } from './voiture.service';
import { VoitureComponent } from './voiture.component';
import { VoitureDetailComponent } from './voiture-detail.component';
import { VoitureUpdateComponent } from './voiture-update.component';

@Injectable({ providedIn: 'root' })
export class VoitureResolve implements Resolve<IVoiture> {
  constructor(private service: VoitureService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVoiture> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((voiture: HttpResponse<Voiture>) => {
          if (voiture.body) {
            return of(voiture.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Voiture());
  }
}

export const voitureRoute: Routes = [
  {
    path: '',
    component: VoitureComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'coviteApp.voiture.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: VoitureDetailComponent,
    resolve: {
      voiture: VoitureResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'coviteApp.voiture.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: VoitureUpdateComponent,
    resolve: {
      voiture: VoitureResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'coviteApp.voiture.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: VoitureUpdateComponent,
    resolve: {
      voiture: VoitureResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'coviteApp.voiture.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
