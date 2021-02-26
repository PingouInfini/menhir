import { IGroupe } from 'app/shared/model/groupe.model';

export interface ILieu {
  id?: number;
  nom?: string;
  latitude?: number;
  longitude?: number;
  groupes?: IGroupe[];
}

export class Lieu implements ILieu {
  constructor(public id?: number, public nom?: string, public latitude?: number, public longitude?: number, public groupes?: IGroupe[]) {}
}
