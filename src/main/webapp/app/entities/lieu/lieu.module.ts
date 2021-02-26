import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MenhirSharedModule } from 'app/shared/shared.module';
import { LieuComponent } from './lieu.component';
import { LieuDetailComponent } from './lieu-detail.component';
import { LieuUpdateComponent } from './lieu-update.component';
import { LieuDeleteDialogComponent } from './lieu-delete-dialog.component';
import { lieuRoute } from './lieu.route';

@NgModule({
  imports: [MenhirSharedModule, RouterModule.forChild(lieuRoute)],
  declarations: [LieuComponent, LieuDetailComponent, LieuUpdateComponent, LieuDeleteDialogComponent],
  entryComponents: [LieuDeleteDialogComponent],
})
export class MenhirLieuModule {}
