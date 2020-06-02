import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'commande',
        loadChildren: () => import('./commande/commande.module').then(m => m.CoviteCommandeModule)
      },
      {
        path: 'voiture',
        loadChildren: () => import('./voiture/voiture.module').then(m => m.CoviteVoitureModule)
      },
      {
        path: 'position',
        loadChildren: () => import('./position/position.module').then(m => m.CovitePositionModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class CoviteEntityModule {}
