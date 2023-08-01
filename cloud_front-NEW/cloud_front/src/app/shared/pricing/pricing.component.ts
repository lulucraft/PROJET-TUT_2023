import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/internal/Observable';
import { Offer } from 'src/app/models/offer';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-pricing',
  templateUrl: './pricing.component.html',
  styleUrls: ['./pricing.component.scss']
})
export class PricingComponent {

  public offers$?: Observable<Offer[]>;
  public offers: Offer[] = [];

  constructor(private router: Router, private dataService: DataService) { }

  ngOnInit(): void {
    this.offers$ = this.dataService.getOffers();
    // this.offersObservable.subscribe((offers: Offer[]) => {
    //   this.offers = offers;
    // });
  }

  choiceOffer(offer: number): void {
    this.router.navigate(['/checkout', offer]);
    // if (offer === 1) {
    // } else if (offer === 2) {
    // }
  }
}
