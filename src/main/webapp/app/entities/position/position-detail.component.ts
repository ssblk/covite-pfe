import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPosition } from 'app/shared/model/position.model';

@Component({
  selector: 'jhi-position-detail',
  templateUrl: './position-detail.component.html'
})
export class PositionDetailComponent implements OnInit {
  position: IPosition | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ position }) => (this.position = position));
  }

  previousState(): void {
    window.history.back();
  }
}
