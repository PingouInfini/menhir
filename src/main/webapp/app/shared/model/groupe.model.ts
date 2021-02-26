import { Moment } from 'moment';
import { ILieu } from 'app/shared/model/lieu.model';
import { IIndividu } from 'app/shared/model/individu.model';

export interface IGroupe {
  id?: number;
  nom?: string;
  description?: string;
  adresse?: string;
  dateCreation?: Moment;
  pieceJointeContentType?: string;
  pieceJointe?: any;
  estSitues?: ILieu[];
  individus?: IIndividu[];
}

export class Groupe implements IGroupe {
  constructor(
    public id?: number,
    public nom?: string,
    public description?: string,
    public adresse?: string,
    public dateCreation?: Moment,
    public pieceJointeContentType?: string,
    public pieceJointe?: any,
    public estSitues?: ILieu[],
    public individus?: IIndividu[]
  ) {}
}
