import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IIndividu } from 'app/shared/model/individu.model';
import { IndividuService } from './individu.service';

@Component({
  templateUrl: './individu-delete-dialog.component.html',
})
export class IndividuDeleteDialogComponent {
  individu?: IIndividu;

  constructor(protected individuService: IndividuService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.individuService.delete(id).subscribe(() => {
      this.eventManager.broadcast('individuListModification');
      this.activeModal.close();
    });
  }
}
