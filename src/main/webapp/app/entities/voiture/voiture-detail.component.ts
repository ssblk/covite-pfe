import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVoiture } from 'app/shared/model/voiture.model';

@Component({
  selector: 'jhi-voiture-detail',
  templateUrl: './voiture-detail.component.html'
})
export class VoitureDetailComponent implements OnInit {
  voiture: IVoiture | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ voiture }) => (this.voiture = voiture));
  }

  previousState(): void {
    window.history.back();
  }
}
