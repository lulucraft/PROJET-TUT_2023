import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Navigation, Params, Router } from '@angular/router';
import { ICancelCallbackData, ICreateOrderRequest, IOnApproveCallbackActions, IOnApproveCallbackData, IPayPalConfig } from 'ngx-paypal';
import { Country } from '../../../models/country';
import { Offer } from '../../../models/offer';
import { AuthService } from '../../../services/auth.service';
import { DataService, countries } from '../../../services/data.service';
import { MatStepper } from '@angular/material/stepper';
import { MatSnackBar } from '@angular/material/snack-bar';
import { JWTToken } from '../../../models/jwt-token';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {

  @ViewChild('stepper') private stepper!: MatStepper;

  public firstFormGroup: FormGroup = this.formBuilder.group({
    lastnameCtrl: ['', Validators.required],
    nameCtrl: ['', Validators.required],
    addressCtrl: ['', Validators.required],
    postalCodeCtrl: ['', Validators.required],
    cityCtrl: ['', Validators.required],
    countryCtrl: ['', Validators.required],
  });

  public countries: Country[] = [];
  public offer?: Offer;

  public payPalConfig?: IPayPalConfig;

  public quantity: number = 1;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    let navig: Navigation | null = this.router.getCurrentNavigation();
    if (navig && navig.extras && navig.extras.state) {
      this.quantity = navig.extras.state['qty'];
      console.log(this.quantity)
    }
  }

  ngOnInit(): void {
    if (this.authService.currentUserValue?.offer) {
      alert("Vous avez déjà une offre")
      // User already has an offer
      this.home();
      return;
    }

    this.route.params.subscribe((params: Params) => {
      let id = params['id'];

      if (!id) {
        alert("Vous n'avez pas sélectionné d'offre");
        // Redirect to home if no offer selected
        this.router.navigate(['/offer']);
        return;
      }

      // Get Offer from database
      this.dataService.getOffer(parseInt(id)).subscribe({
        next: async (offer: Offer) => {
          this.offer = offer;

          await this.loadPayPalConfig();

          this.countries = countries;

          let user = this.authService.currentUserValue;

          if (user) {
            // Set user details from connected user data
            this.firstFormGroup.patchValue({
              nameCtrl: user.firstname,
              lastnameCtrl: user.lastname,
              addressCtrl: user.address,
              postalCodeCtrl: user.postalCode,
              cityCtrl: user.city,
            });

            // Set user country from connected user data
            if (user.country) {
              let country = this.countries.find(c => c.name === user!.country);

              this.firstFormGroup.patchValue({
                countryCtrl: country
              });
            } else {
              this.initDefaultCountry();
            }
          } else {
            this.initDefaultCountry();
          }
        },
        error: (err: any) => {
          console.error(err);
          this.snackBar.open("Erreur lors de la récupération de l'offre " + id, '', { duration: 1700, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
        }
      });

      // if (id == 1) {
      //   // Offer particulier
      // } else if (id == 2) {
      //   // Offer professionel
      // } else {
      //   alert("Offre invalide");
      //   this.home();
      // }
    });

  }

  // toInt(input: string) {
  //   return parseInt(input);
  // }

  // async ngAfterViewInit() {
  //   this.route.params.subscribe((params: Params) => {
  //     let id = params['id'];

  //     if (!id) {
  //       alert("Vous n'avez pas sélectionné d'offre")
  //       // Redirect to home if no offer selected
  //       this.router.navigate(['/offer']);
  //       return;
  //     }

  //     // Get Offer from database
  //     this.offerObservable = this.dataService.getOffer(parseInt(id));
  //     this.offerObservable.subscribe({
  //       next: async (offer: Offer) => {
  //         this.offer = offer;

  //         await this.loadPayPal();

  //         this.countries = countries;

  //         let user = this.authService.currentUserValue;

  //         if (user) {
  //           // Set user details from connected user data
  //           this.firstFormGroup.patchValue({
  //             nameCtrl: user.firstname,
  //             lastnameCtrl: user.lastname,
  //             addressCtrl: user.address,
  //             postalCodeCtrl: user.postalCode,
  //             cityCtrl: user.city,
  //           });

  //           // Set user country from connected user data
  //           if (user.country) {
  //             let country = this.countries.find(c => c.name === user!.country);

  //             this.firstFormGroup.patchValue({
  //               countryCtrl: country
  //             });
  //           } else {
  //             this.initDefaultCountry();
  //           }
  //         } else {
  //           this.initDefaultCountry();
  //         }
  //       },
  //       error: (err: any) => {
  //         console.error(err);
  //         alert("Erreur lors de la récupération de l'offre " + id);
  //       }
  //     });

  //     if (id == 1) {
  //       // Offer particulier
  //     } else if (id == 2) {
  //       // Offer professionel
  //     } else {
  //       alert("Offre invalide");
  //       this.home();
  //     }
  //   });

  // }

  async loadPayPalConfig(): Promise<void> {
    if (!this.offer) {
      alert("Offre non chargée");
      return;
    }

    let offer = this.offer!;
    let totalPrice = offer.price * this.quantity;

    this.payPalConfig = {
      currency: 'EUR',
      clientId: 'AaD_eArL3lImSsUm6EPqC1XPhS6TZ1wkNt7DEamO8lUUJw9xQ1gf-_qvW4iAeFu3VZsJR61-NN5Qo1AF',
      createOrderOnClient: (data) => <ICreateOrderRequest>{
        intent: 'CAPTURE',
        purchase_units: [{
          amount: {
            value: totalPrice.toString(),
            currency_code: "EUR",
            breakdown: {
              item_total: {
                value: totalPrice.toString(),
                currency_code: "EUR"
              }
            }
          },
          items: [{
            name: offer.name,
            quantity: this.quantity.toString(),
            unit_amount: {
              currency_code: "EUR",
              value: offer.price.toString()
            }
          }]
        }]
      },
      advanced: {
        commit: 'true'
      },
      style: {
        layout: "vertical",
        color: "blue",
        shape: "rect",
        label: "paypal"
      },
      onApprove: async (data: IOnApproveCallbackData, actions: IOnApproveCallbackActions) => {
        console.log("Subscription ID: " + data.subscriptionID);
        console.log("Order ID: " + data.orderID);
        console.log("Payer ID: " + data.payerID);

        if (!actions) {
          throw new Error("No actions");
        }

        if (!actions.order) {
          throw new Error("No order");
        }

        const details: any = await actions.order.capture();
        // alert("Transaction completed by " + details.payer.name.given_name);
        console.log(details);
        let order = await actions.order.get();
        console.log("Order ID: " + order.id);

        // Send order to backend
        // let paypalProducts: PurchaseItem[] | undefined = [];

        // if (order.purchase_units && order.purchase_units.length > 0) {
        //   paypalProducts = order.purchase_units[0].items;
        //.items.map(i => {
        //   return {
        //     product: i,
        //     quantity: parseInt(i.quantity)
        //   }
        // }).filter(p => p.quantity > 0)
        // }

        this.dataService.sendOrder({
          paypalId: order.id,
          date: new Date(),
          quantity: this.quantity
          // products: paypalProducts
        }).subscribe(() => {
          // Refresh token to set new offer in user token
          this.authService.refreshTokenRequest().subscribe({
            next: (token: JWTToken) => {
              this.authService.saveRefreshToken(token);
              console.log("User offer updated");
              this.snackBar.open('Votre compte a été mis à jour', '', { duration: 1700, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });
            },
            error: () => {
              this.authService.logout();
              this.snackBar.open('Veuillez vous reconnecter pour mettre à jour votre compte', '', { duration: 1700, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            }
          });
        });

        // Change the route to the success page
        this.stepper.next();
      },
      onCancel: (data: ICancelCallbackData, actions: any) => {
        console.log("OnCancel", data, actions);
        // alert("Transaction cancelled");
      },
      onError: (err: any) => {
        console.error(err);
      }
    }
  }

  initDefaultCountry(): void {
    // Set default country to France
    let country = this.countries.find(c => c.code === 'FR');
    if (country) {
      this.firstFormGroup.patchValue({
        countryCtrl: country
      });
      // this.firstFormGroup.controls['countryCtrl'].setValue(country);
    }
  }

  home(): void {
    this.router.navigate(['/']);
  }

}
