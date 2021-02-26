import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IIndividu, Individu } from 'app/shared/model/individu.model';
import { IndividuService } from './individu.service';
import { IndividuComponent } from './individu.component';
import { IndividuDetailComponent } from './individu-detail.component';
import { IndividuUpdateComponent } from './individu-update.component';

@Injectable({ providedIn: 'root' })
export class IndividuResolve implements Resolve<IIndividu> {
  constructor(private service: IndividuService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IIndividu> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((individu: HttpResponse<Individu>) => {
          if (individu.body) {
            return of(individu.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Individu());
  }
}

export const individuRoute: Routes = [
  {
    path: '',
    component: IndividuComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'menhirApp.individu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: IndividuDetailComponent,
    resolve: {
      individu: IndividuResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'menhirApp.individu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: IndividuUpdateComponent,
    resolve: {
      individu: IndividuResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'menhirApp.individu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: IndividuUpdateComponent,
    resolve: {
      individu: IndividuResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'menhirApp.individu.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
