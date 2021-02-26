import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IIndividu } from 'app/shared/model/individu.model';

type EntityResponseType = HttpResponse<IIndividu>;
type EntityArrayResponseType = HttpResponse<IIndividu[]>;

@Injectable({ providedIn: 'root' })
export class IndividuService {
  public resourceUrl = SERVER_API_URL + 'api/individus';

  constructor(protected http: HttpClient) {}

  create(individu: IIndividu): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(individu);
    return this.http
      .post<IIndividu>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(individu: IIndividu): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(individu);
    return this.http
      .put<IIndividu>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IIndividu>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IIndividu[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(individu: IIndividu): IIndividu {
    const copy: IIndividu = Object.assign({}, individu, {
      dateDeNaissance: individu.dateDeNaissance && individu.dateDeNaissance.isValid() ? individu.dateDeNaissance.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateDeNaissance = res.body.dateDeNaissance ? moment(res.body.dateDeNaissance) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((individu: IIndividu) => {
        individu.dateDeNaissance = individu.dateDeNaissance ? moment(individu.dateDeNaissance) : undefined;
      });
    }
    return res;
  }
}
