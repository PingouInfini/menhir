import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IGroupe } from 'app/shared/model/groupe.model';

type EntityResponseType = HttpResponse<IGroupe>;
type EntityArrayResponseType = HttpResponse<IGroupe[]>;

@Injectable({ providedIn: 'root' })
export class GroupeService {
  public resourceUrl = SERVER_API_URL + 'api/groupes';

  constructor(protected http: HttpClient) {}

  create(groupe: IGroupe): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(groupe);
    return this.http
      .post<IGroupe>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(groupe: IGroupe): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(groupe);
    return this.http
      .put<IGroupe>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IGroupe>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IGroupe[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(groupe: IGroupe): IGroupe {
    const copy: IGroupe = Object.assign({}, groupe, {
      dateCreation: groupe.dateCreation && groupe.dateCreation.isValid() ? groupe.dateCreation.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateCreation = res.body.dateCreation ? moment(res.body.dateCreation) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((groupe: IGroupe) => {
        groupe.dateCreation = groupe.dateCreation ? moment(groupe.dateCreation) : undefined;
      });
    }
    return res;
  }
}
