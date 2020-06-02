import { ICommande } from 'app/shared/model/commande.model';

export interface IPosition {
  id?: number;
  pointdepart?: string;
  pointarrive?: string;
  commandes?: ICommande[];
}

export class Position implements IPosition {
  constructor(public id?: number, public pointdepart?: string, public pointarrive?: string, public commandes?: ICommande[]) {}
}
