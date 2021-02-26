import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IIndividu, Individu } from 'app/shared/model/individu.model';
import { IndividuService } from './individu.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IGroupe } from 'app/shared/model/groupe.model';
import { GroupeService } from 'app/entities/groupe/groupe.service';

@Component({
  selector: 'jhi-individu-update',
  templateUrl: './individu-update.component.html',
})
export class IndividuUpdateComponent implements OnInit {
  isSaving = false;
  groupes: IGroupe[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    taille: [],
    dateDeNaissance: [],
    couleurCheveux: [],
    coiffure: [],
    photo: [],
    photoContentType: [],
    appartientAS: [],
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected individuService: IndividuService,
    protected groupeService: GroupeService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ individu }) => {
      if (!individu.id) {
        const today = moment().startOf('day');
        individu.dateDeNaissance = today;
      }

      this.updateForm(individu);

      this.groupeService.query().subscribe((res: HttpResponse<IGroupe[]>) => (this.groupes = res.body || []));
    });
  }

  updateForm(individu: IIndividu): void {
    this.editForm.patchValue({
      id: individu.id,
      nom: individu.nom,
      taille: individu.taille,
      dateDeNaissance: individu.dateDeNaissance ? individu.dateDeNaissance.format(DATE_TIME_FORMAT) : null,
      couleurCheveux: individu.couleurCheveux,
      coiffure: individu.coiffure,
      photo: individu.photo,
      photoContentType: individu.photoContentType,
      appartientAS: individu.appartientAS,
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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (this.elementRef && idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const individu = this.createFromForm();
    if (individu.id !== undefined) {
      this.subscribeToSaveResponse(this.individuService.update(individu));
    } else {
      this.subscribeToSaveResponse(this.individuService.create(individu));
    }
  }

  private createFromForm(): IIndividu {
    return {
      ...new Individu(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      taille: this.editForm.get(['taille'])!.value,
      dateDeNaissance: this.editForm.get(['dateDeNaissance'])!.value
        ? moment(this.editForm.get(['dateDeNaissance'])!.value, DATE_TIME_FORMAT)
        : undefined,
      couleurCheveux: this.editForm.get(['couleurCheveux'])!.value,
      coiffure: this.editForm.get(['coiffure'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      appartientAS: this.editForm.get(['appartientAS'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIndividu>>): void {
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

  trackById(index: number, item: IGroupe): any {
    return item.id;
  }

  getSelected(selectedVals: IGroupe[], option: IGroupe): IGroupe {
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
