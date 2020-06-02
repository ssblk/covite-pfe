import { Moment } from 'moment';
import { IVoiture } from 'app/shared/model/voiture.model';
import { IPosition } from 'app/shared/model/position.model';
import { Categorie } from 'app/shared/model/enumerations/categorie.model';
import { Service } from 'app/shared/model/enumerations/service.model';

export interface ICommande {
  id?: number;
  date?: Moment;
  prix?: number;
  type?: Categorie;
  typeservice?: Service;
  voiture?: IVoiture;
  position?: IPosition;
}

export class Commande implements ICommande {
  constructor(
    public id?: number,
    public date?: Moment,
    public prix?: number,
    public type?: Categorie,
    public typeservice?: Service,
    public voiture?: IVoiture,
    public position?: IPosition
  ) {}
}
