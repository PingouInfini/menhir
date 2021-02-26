import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IGroupe, Groupe } from 'app/shared/model/groupe.model';
import { GroupeService } from './groupe.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { ILieu } from 'app/shared/model/lieu.model';
import { LieuService } from 'app/entities/lieu/lieu.service';

@Component({
  selector: 'jhi-groupe-update',
  templateUrl: './groupe-update.component.html',
})
export class GroupeUpdateComponent implements OnInit {
  isSaving = false;
  lieus: ILieu[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    description: [],
    adresse: [],
    dateCreation: [],
    pieceJointe: [],
    pieceJointeContentType: [],
    estSitues: [],
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected groupeService: GroupeService,
    protected lieuService: LieuService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ groupe }) => {
      if (!groupe.id) {
        const today = moment().startOf('day');
        groupe.dateCreation = today;
      }

      this.updateForm(groupe);

      this.lieuService.query().subscribe((res: HttpResponse<ILieu[]>) => (this.lieus = res.body || []));
    });
  }

  updateForm(groupe: IGroupe): void {
    this.editForm.patchValue({
      id: groupe.id,
      nom: groupe.nom,
      description: groupe.description,
      adresse: groupe.adresse,
      dateCreation: groupe.dateCreation ? groupe.dateCreation.format(DATE_TIME_FORMAT) : null,
      pieceJointe: groupe.pieceJointe,
      pieceJointeContentType: groupe.pieceJointeContentType,
      estSitues: groupe.estSitues,
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: any, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('menhirApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const groupe = this.createFromForm();
    if (groupe.id !== undefined) {
      this.subscribeToSaveResponse(this.groupeService.update(groupe));
    } else {
      this.subscribeToSaveResponse(this.groupeService.create(groupe));
    }
  }

  private createFromForm(): IGroupe {
    return {
      ...new Groupe(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      description: this.editForm.get(['description'])!.value,
      adresse: this.editForm.get(['adresse'])!.value,
      dateCreation: this.editForm.get(['dateCreation'])!.value
        ? moment(this.editForm.get(['dateCreation'])!.value, DATE_TIME_FORMAT)
        : undefined,
      pieceJointeContentType: this.editForm.get(['pieceJointeContentType'])!.value,
      pieceJointe: this.editForm.get(['pieceJointe'])!.value,
      estSitues: this.editForm.get(['estSitues'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGroupe>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: ILieu): any {
    return item.id;
  }

  getSelected(selectedVals: ILieu[], option: ILieu): ILieu {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
