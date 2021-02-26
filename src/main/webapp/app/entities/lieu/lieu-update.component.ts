import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ILieu, Lieu } from 'app/shared/model/lieu.model';
import { LieuService } from './lieu.service';

@Component({
  selector: 'jhi-lieu-update',
  templateUrl: './lieu-update.component.html',
})
export class LieuUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nom: [],
    latitude: [],
    longitude: [],
  });

  constructor(protected lieuService: LieuService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lieu }) => {
      this.updateForm(lieu);
    });
  }

  updateForm(lieu: ILieu): void {
    this.editForm.patchValue({
      id: lieu.id,
      nom: lieu.nom,
      latitude: lieu.latitude,
      longitude: lieu.longitude,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lieu = this.createFromForm();
    if (lieu.id !== undefined) {
      this.subscribeToSaveResponse(this.lieuService.update(lieu));
    } else {
      this.subscribeToSaveResponse(this.lieuService.create(lieu));
    }
  }

  private createFromForm(): ILieu {
    return {
      ...new Lieu(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      latitude: this.editForm.get(['latitude'])!.value,
      longitude: this.editForm.get(['longitude'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILieu>>): void {
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
}
