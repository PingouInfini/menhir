import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { MenhirTestModule } from '../../../test.module';
import { GroupeDetailComponent } from 'app/entities/groupe/groupe-detail.component';
import { Groupe } from 'app/shared/model/groupe.model';

describe('Component Tests', () => {
  describe('Groupe Management Detail Component', () => {
    let comp: GroupeDetailComponent;
    let fixture: ComponentFixture<GroupeDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ groupe: new Groupe(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MenhirTestModule],
        declarations: [GroupeDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(GroupeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GroupeDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load groupe on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.groupe).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
