import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/internal/Observable';
import { Offer } from 'src/app/models/offer';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-pricing',
  templateUrl: './pricing.component.html',
  styleUrls: ['./pricing.component.scss']
})
export class PricingComponent {

  public offers$?: Observable<Offer[]>;
  public offers: Offer[] = [];

  constructor(private router: Router, private dataService: DataService, private authService: AuthService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.offers$ = this.dataService.getOffers();
    // this.offersObservable.subscribe((offers: Offer[]) => {
    //   this.offers = offers;
    // });
  }

  choiceOffer(offer: number, qty: string): void {
    if (!this.authService.isAuthenticated()) {
      this.snackBar.open("Veuillez vous connecter pour finaliser la commande", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
      this.router.navigate(['/login']);
      return;
    }

    this.router.navigate(['/user/checkout', offer], { state: { qty: qty }});
    // if (offer === 1) {
    // } else if (offer === 2) {
    // }
  }
}
