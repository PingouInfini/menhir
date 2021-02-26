import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ILieu } from 'app/shared/model/lieu.model';

type EntityResponseType = HttpResponse<ILieu>;
type EntityArrayResponseType = HttpResponse<ILieu[]>;

@Injectable({ providedIn: 'root' })
export class LieuService {
  public resourceUrl = SERVER_API_URL + 'api/lieus';

  constructor(protected http: HttpClient) {}

  create(lieu: ILieu): Observable<EntityResponseType> {
    return this.http.post<ILieu>(this.resourceUrl, lieu, { observe: 'response' });
  }

  update(lieu: ILieu): Observable<EntityResponseType> {
    return this.http.put<ILieu>(this.resourceUrl, lieu, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILieu>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILieu[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
