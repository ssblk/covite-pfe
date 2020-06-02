import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CoviteTestModule } from '../../../test.module';
import { VoitureDetailComponent } from 'app/entities/voiture/voiture-detail.component';
import { Voiture } from 'app/shared/model/voiture.model';

describe('Component Tests', () => {
  describe('Voiture Management Detail Component', () => {
    let comp: VoitureDetailComponent;
    let fixture: ComponentFixture<VoitureDetailComponent>;
    const route = ({ data: of({ voiture: new Voiture(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CoviteTestModule],
        declarations: [VoitureDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(VoitureDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(VoitureDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load voiture on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.voiture).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
