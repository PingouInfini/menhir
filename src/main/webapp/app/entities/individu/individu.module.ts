import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MenhirSharedModule } from 'app/shared/shared.module';
import { IndividuComponent } from './individu.component';
import { IndividuDetailComponent } from './individu-detail.component';
import { IndividuUpdateComponent } from './individu-update.component';
import { IndividuDeleteDialogComponent } from './individu-delete-dialog.component';
import { individuRoute } from './individu.route';

@NgModule({
  imports: [MenhirSharedModule, RouterModule.forChild(individuRoute)],
  declarations: [IndividuComponent, IndividuDetailComponent, IndividuUpdateComponent, IndividuDeleteDialogComponent],
  entryComponents: [IndividuDeleteDialogComponent],
})
export class MenhirIndividuModule {}
