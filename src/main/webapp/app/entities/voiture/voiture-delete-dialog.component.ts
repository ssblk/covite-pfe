import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IVoiture } from 'app/shared/model/voiture.model';
import { VoitureService } from './voiture.service';

@Component({
  templateUrl: './voiture-delete-dialog.component.html'
})
export class VoitureDeleteDialogComponent {
  voiture?: IVoiture;

  constructor(protected voitureService: VoitureService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.voitureService.delete(id).subscribe(() => {
      this.eventManager.broadcast('voitureListModification');
      this.activeModal.close();
    });
  }
}
