import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IPosition, Position } from 'app/shared/model/position.model';
import { PositionService } from './position.service';
import { PositionComponent } from './position.component';
import { PositionDetailComponent } from './position-detail.component';
import { PositionUpdateComponent } from './position-update.component';

@Injectable({ providedIn: 'root' })
export class PositionResolve implements Resolve<IPosition> {
  constructor(private service: PositionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPosition> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((position: HttpResponse<Position>) => {
          if (position.body) {
            return of(position.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Position());
  }
}

export const positionRoute: Routes = [
  {
    path: '',
    component: PositionComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'coviteApp.position.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PositionDetailComponent,
    resolve: {
      position: PositionResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'coviteApp.position.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PositionUpdateComponent,
    resolve: {
      position: PositionResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'coviteApp.position.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PositionUpdateComponent,
    resolve: {
      position: PositionResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'coviteApp.position.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
