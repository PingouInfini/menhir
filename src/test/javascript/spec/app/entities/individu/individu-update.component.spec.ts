import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MenhirTestModule } from '../../../test.module';
import { IndividuUpdateComponent } from 'app/entities/individu/individu-update.component';
import { IndividuService } from 'app/entities/individu/individu.service';
import { Individu } from 'app/shared/model/individu.model';

describe('Component Tests', () => {
  describe('Individu Management Update Component', () => {
    let comp: IndividuUpdateComponent;
    let fixture: ComponentFixture<IndividuUpdateComponent>;
    let service: IndividuService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MenhirTestModule],
        declarations: [IndividuUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(IndividuUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(IndividuUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(IndividuService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Individu(123);
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
        const entity = new Individu();
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
