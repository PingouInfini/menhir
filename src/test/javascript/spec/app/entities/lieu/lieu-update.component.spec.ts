import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MenhirTestModule } from '../../../test.module';
import { LieuUpdateComponent } from 'app/entities/lieu/lieu-update.component';
import { LieuService } from 'app/entities/lieu/lieu.service';
import { Lieu } from 'app/shared/model/lieu.model';

describe('Component Tests', () => {
  describe('Lieu Management Update Component', () => {
    let comp: LieuUpdateComponent;
    let fixture: ComponentFixture<LieuUpdateComponent>;
    let service: LieuService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MenhirTestModule],
        declarations: [LieuUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(LieuUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LieuUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LieuService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Lieu(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Lieu();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
