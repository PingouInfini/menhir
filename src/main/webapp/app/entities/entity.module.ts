import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'individu',
        loadChildren: () => import('./individu/individu.module').then(m => m.MenhirIndividuModule),
      },
      {
        path: 'groupe',
        loadChildren: () => import('./groupe/groupe.module').then(m => m.MenhirGroupeModule),
      },
      {
        path: 'lieu',
        loadChildren: () => import('./lieu/lieu.module').then(m => m.MenhirLieuModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class MenhirEntityModule {}
