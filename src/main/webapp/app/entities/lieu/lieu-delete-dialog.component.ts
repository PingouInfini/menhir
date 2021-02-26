import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILieu } from 'app/shared/model/lieu.model';
import { LieuService } from './lieu.service';

@Component({
  templateUrl: './lieu-delete-dialog.component.html',
})
export class LieuDeleteDialogComponent {
  lieu?: ILieu;

  constructor(protected lieuService: LieuService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.lieuService.delete(id).subscribe(() => {
      this.eventManager.broadcast('lieuListModification');
      this.activeModal.close();
    });
  }
}
