import { Moment } from 'moment';
import { IGroupe } from 'app/shared/model/groupe.model';
import { Couleur } from 'app/shared/model/enumerations/couleur.model';

export interface IIndividu {
  id?: number;
  nom?: string;
  taille?: number;
  dateDeNaissance?: Moment;
  couleurCheveux?: Couleur;
  coiffure?: string;
  photoContentType?: string;
  photo?: any;
  appartientAS?: IGroupe[];
}

export class Individu implements IIndividu {
  constructor(
    public id?: number,
    public nom?: string,
    public taille?: number,
    public dateDeNaissance?: Moment,
    public couleurCheveux?: Couleur,
    public coiffure?: string,
    public photoContentType?: string,
    public photo?: any,
    public appartientAS?: IGroupe[]
  ) {}
}
