import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MenhirSharedModule } from 'app/shared/shared.module';
import { GroupeComponent } from './groupe.component';
import { GroupeDetailComponent } from './groupe-detail.component';
import { GroupeUpdateComponent } from './groupe-update.component';
import { GroupeDeleteDialogComponent } from './groupe-delete-dialog.component';
import { groupeRoute } from './groupe.route';

@NgModule({
  imports: [MenhirSharedModule, RouterModule.forChild(groupeRoute)],
  declarations: [GroupeComponent, GroupeDetailComponent, GroupeUpdateComponent, GroupeDeleteDialogComponent],
  entryComponents: [GroupeDeleteDialogComponent],
})
export class MenhirGroupeModule {}
