import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, SearchWithPagination } from 'app/shared/util/request-util';
import { IPosition } from 'app/shared/model/position.model';

type EntityResponseType = HttpResponse<IPosition>;
type EntityArrayResponseType = HttpResponse<IPosition[]>;

@Injectable({ providedIn: 'root' })
export class PositionService {
  public resourceUrl = SERVER_API_URL + 'api/positions';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/positions';

  constructor(protected http: HttpClient) {}

  create(position: IPosition): Observable<EntityResponseType> {
    return this.http.post<IPosition>(this.resourceUrl, position, { observe: 'response' });
  }

  update(position: IPosition): Observable<EntityResponseType> {
    return this.http.put<IPosition>(this.resourceUrl, position, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPosition>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPosition[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPosition[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
