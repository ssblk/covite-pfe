import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CoviteTestModule } from '../../../test.module';
import { VoitureUpdateComponent } from 'app/entities/voiture/voiture-update.component';
import { VoitureService } from 'app/entities/voiture/voiture.service';
import { Voiture } from 'app/shared/model/voiture.model';

describe('Component Tests', () => {
  describe('Voiture Management Update Component', () => {
    let comp: VoitureUpdateComponent;
    let fixture: ComponentFixture<VoitureUpdateComponent>;
    let service: VoitureService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CoviteTestModule],
        declarations: [VoitureUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(VoitureUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(VoitureUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VoitureService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Voiture(123);
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
        const entity = new Voiture();
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
