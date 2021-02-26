import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ILieu, Lieu } from 'app/shared/model/lieu.model';
import { LieuService } from './lieu.service';
import { LieuComponent } from './lieu.component';
import { LieuDetailComponent } from './lieu-detail.component';
import { LieuUpdateComponent } from './lieu-update.component';

@Injectable({ providedIn: 'root' })
export class LieuResolve implements Resolve<ILieu> {
  constructor(private service: LieuService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILieu> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((lieu: HttpResponse<Lieu>) => {
          if (lieu.body) {
            return of(lieu.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Lieu());
  }
}

export const lieuRoute: Routes = [
  {
    path: '',
    component: LieuComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'menhirApp.lieu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LieuDetailComponent,
    resolve: {
      lieu: LieuResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'menhirApp.lieu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LieuUpdateComponent,
    resolve: {
      lieu: LieuResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'menhirApp.lieu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LieuUpdateComponent,
    resolve: {
      lieu: LieuResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'menhirApp.lieu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
