import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Newsletter } from 'src/app/models/newsletter';
import { NewsletterType } from 'src/app/models/newsletter-type';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.scss']
})
export class NewsletterComponent implements OnInit {

  @ViewChild('autosize') autosize!: CdkTextareaAutosize;

  public formGroupInfos: FormGroup = this.formBuilder.group({
    title: ['', Validators.required],
    text: ['', Validators.required]
  });
  public newsletterInfos: Newsletter = { title: '', text: '', type: NewsletterType.INFOS };

  public formGroupSells: FormGroup = this.formBuilder.group({
    title: ['', Validators.required],
    january: [0],
    february: [0],
    march: [0],
    april: [0],
    may: [0],
    june: [0],
    july: [0],
    august: [0],
    september: [0],
    october: [0],
    november: [0],
    december: [0]
  });
  public newsletterSells: Newsletter = { title: '', text: '', type: NewsletterType.SELLS };

  constructor(private formBuilder: FormBuilder, private router: Router, private dataService: DataService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.dataService.getNewsletters().subscribe((newsletters: Newsletter[]) => {
      if (!newsletters) {
        alert('Impossible de charger la newsletter');
        return;
      }

      for (let newsletter of newsletters) {
        if (newsletter.type === NewsletterType.INFOS) {
          this.newsletterInfos = newsletter;

          this.formGroupInfos.get('title')!.setValue(newsletter.title);
          this.formGroupInfos.get('text')!.setValue(newsletter.text);
        } else if (newsletter.type === NewsletterType.SELLS) {
          this.newsletterSells = newsletter;

          this.formGroupSells.get('title')!.setValue(newsletter.title);

          let sells: string[] = newsletter.text.replace(' ', '').split(',');
          this.formGroupSells.get('january')!.setValue(sells[0]);
          this.formGroupSells.get('february')!.setValue(sells[1]);
          this.formGroupSells.get('march')!.setValue(sells[2]);
          this.formGroupSells.get('april')!.setValue(sells[3]);
          this.formGroupSells.get('may')!.setValue(sells[4]);
          this.formGroupSells.get('june')!.setValue(sells[5]);
          this.formGroupSells.get('july')!.setValue(sells[6]);
          this.formGroupSells.get('august')!.setValue(sells[7]);
          this.formGroupSells.get('september')!.setValue(sells[8]);
          this.formGroupSells.get('october')!.setValue(sells[9]);
          this.formGroupSells.get('november')!.setValue(sells[10]);
          this.formGroupSells.get('december')!.setValue(sells[11]);
        }
      }

    });
  }

  publishInfosNewsletter(): void {
    if (this.formGroupInfos.get('title')!.invalid || this.formGroupInfos.get('text')!.invalid) {
      this.snackBar.open('Veuillez remplir tous les champs', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    this.newsletterInfos.title = this.formGroupInfos.get('title')!.value;
    this.newsletterInfos.text = this.formGroupInfos.get('text')!.value;

    this.dataService.publishNewsletter(this.newsletterInfos).subscribe(() => {
      this.router.navigate(['/main/newsletter']);
    });
  }

  publishSellsNewsletter(): void {
    if (
      this.formGroupSells.get('title')!.invalid //||
      // this.formGroupSells.get('january')!.invalid ||
      // this.formGroupSells.get('february')!.invalid ||
      // this.formGroupSells.get('march')!.invalid ||
      // this.formGroupSells.get('april')!.invalid ||
      // this.formGroupSells.get('may')!.invalid ||
      // this.formGroupSells.get('june')!.invalid ||
      // this.formGroupSells.get('july')!.invalid ||
      // this.formGroupSells.get('august')!.invalid ||
      // this.formGroupSells.get('september')!.invalid ||
      // this.formGroupSells.get('october')!.invalid ||
      // this.formGroupSells.get('november')!.invalid ||
      // this.formGroupSells.get('december')!.invalid
    ) {
      this.snackBar.open('Veuillez remplir tous les champs', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    this.newsletterSells.title = this.formGroupSells.get('title')!.value;
    this.newsletterSells.text = (
      this.formGroupSells.get('january')!.value + ',' +
      this.formGroupSells.get('february')!.value + ',' +
      this.formGroupSells.get('march')!.value + ',' +
      this.formGroupSells.get('april')!.value + ',' +
      this.formGroupSells.get('may')!.value + ',' +
      this.formGroupSells.get('june')!.value + ',' +
      this.formGroupSells.get('july')!.value + ',' +
      this.formGroupSells.get('august')!.value + ',' +
      this.formGroupSells.get('september')!.value + ',' +
      this.formGroupSells.get('october')!.value + ',' +
      this.formGroupSells.get('november')!.value + ',' +
      this.formGroupSells.get('december')!.value
    );

    this.dataService.publishNewsletter(this.newsletterSells).subscribe(() => {
      this.router.navigate(['/main/newsletter']);
    });
  }

}
