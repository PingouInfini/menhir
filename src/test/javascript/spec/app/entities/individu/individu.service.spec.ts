import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { IndividuService } from 'app/entities/individu/individu.service';
import { IIndividu, Individu } from 'app/shared/model/individu.model';
import { Couleur } from 'app/shared/model/enumerations/couleur.model';

describe('Service Tests', () => {
  describe('Individu Service', () => {
    let injector: TestBed;
    let service: IndividuService;
    let httpMock: HttpTestingController;
    let elemDefault: IIndividu;
    let expectedResult: IIndividu | IIndividu[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(IndividuService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Individu(0, 'AAAAAAA', 0, currentDate, Couleur.AUTRE, 'AAAAAAA', 'image/png', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            dateDeNaissance: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Individu', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            dateDeNaissance: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateDeNaissance: currentDate,
          },
          returnedFromService
        );

        service.create(new Individu()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Individu', () => {
        const returnedFromService = Object.assign(
          {
            nom: 'BBBBBB',
            taille: 1,
            dateDeNaissance: currentDate.format(DATE_TIME_FORMAT),
            couleurCheveux: 'BBBBBB',
            coiffure: 'BBBBBB',
            photo: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateDeNaissance: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Individu', () => {
        const returnedFromService = Object.assign(
          {
            nom: 'BBBBBB',
            taille: 1,
            dateDeNaissance: currentDate.format(DATE_TIME_FORMAT),
            couleurCheveux: 'BBBBBB',
            coiffure: 'BBBBBB',
            photo: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateDeNaissance: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Individu', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
