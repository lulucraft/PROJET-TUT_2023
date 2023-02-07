import { Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { ChartData, ChartOptions, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { Newsletter } from 'src/app/models/newsletter';
import { NewsletterType } from 'src/app/models/newsletter-type';
import { User } from 'src/app/models/user';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.scss']
})
export class NewsletterComponent implements OnInit {

  @ViewChildren(BaseChartDirective) charts!: QueryList<BaseChartDirective>;

  public chartUsersData: ChartData<'line'> = {
    datasets: [
      { data: [], label: 'Nouveaux embauchés', backgroundColor: '#42A5F5', borderColor: '#42A5F5', pointBackgroundColor: '#000' }//, fill: false
    ],
    labels: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre']
  };

  public chartSellData: ChartData<'line'> = {
    datasets: [
      { data: [], label: 'Ventes', backgroundColor: '#42A5F5', borderColor: '#42A5F5', pointBackgroundColor: '#000' }//65, 59, 80, 81, 56, 55, 40, 50, 52, 61, 52, 35
    ],
    labels: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre']
  };

  public chartType: ChartType = 'line';
  public chartOptions: ChartOptions = {
    // responsive: true,
    // maintainAspectRatio: false
    scales: {
      y: {
        max: 100,
      }
    }
  };

  // Table new users
  public newUsers: MatTableDataSource<User> = new MatTableDataSource<User>();
  public columnsNewUsers: string[] = ['username', 'date'];

  // Newsletter
  public newsletterInfo?: Newsletter;

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.dataService.getNewUsers().subscribe((users: User[]) => {
      // users.push({ username: 'test5', password: 'null', creationDate: '2020/01/01' });
      // users.push({ username: 'testee', password: 'null', creationDate: '2022/01/01' });
      // users.push({ username: 'testzfe', password: 'null', creationDate: '2021/01/01' });
      // users.push({ username: 'teste', password: 'null', creationDate: '2020/03/01' });
      // users.push({ username: 'test2', password: 'null', creationDate: '2020/02/01' });
      // users.push({ username: 'test', password: 'null', creationDate: '2020/01/01' });
      // users.push({ username: 'test22', password: 'null', creationDate: '2020/04/01' });
      // users.push({ username: 'test3', password: 'null', creationDate: '2022/01/01' });
      // users.push({ username: 'test33', password: 'null', creationDate: '2020/01/02' });

      // Sort users by creation date
      users = users.sort((a: User, b: User) => {
        if (a.creationDate && b.creationDate) {
          return (new Date(a.creationDate).getTime() > new Date(b.creationDate).getTime()) ? -1 : 1;
        } else {
          return -1;
        }
      });

      // Filter the 10 first users
      let nbrNewUsers: number = 0;
      this.newUsers.data = users.filter(() => nbrNewUsers++ < 10);

      // Count the number of new users for each month of the year
      let january: number = 0;
      let february: number = 0;
      let march: number = 0;
      let april: number = 0;
      let may: number = 0;
      let june: number = 0;
      let july: number = 0;
      let august: number = 0;
      let september: number = 0;
      let october: number = 0;
      let november: number = 0;
      let december: number = 0;

      for (let u of users) {
        if (u.creationDate) {
          let month: number = new Date(u.creationDate).getMonth();
          switch (month) {
            case 1:
              january++;
              break;
            case 2:
              february++;
              break;
            case 3:
              march++;
              break;
            case 4:
              april++;
              break;
            case 5:
              may++;
              break;
            case 6:
              june++;
              break;
            case 7:
              july++;
              break;
            case 8:
              august++;
              break;
            case 9:
              september++;
              break;
            case 10:
              october++;
              break;
            case 11:
              november++;
              break;
            case 12:
              december++;
              break;

            default:
              break;
          }
        }
      }

      let chartUsers: BaseChartDirective | undefined = this.charts.get(0);
      if (!chartUsers) {
        throw ('Users chart not found');
      }
      chartUsers!.chart!.data!.datasets[0].data = [january, february, march, april, may, june, july, august, september, october, november, december];
      chartUsers.update();
    });

    // Newsletter
    this.dataService.getNewsletters().subscribe((newsletters: Newsletter[]) => {
      for (let newsletter of newsletters) {
        if (newsletter.type === NewsletterType.INFOS) {
          this.newsletterInfo = newsletter;
        } else if (newsletter.type === NewsletterType.SELLS) {
          let chartSell: BaseChartDirective | undefined = this.charts.get(1);
          if (!chartSell) {
            throw ('Sell chart not found');
          }

          let data: string[] = newsletter.text.replace(' ', '').split(',');
          for (let d of data) {
            // Get selling data from the text
            chartSell.data!.datasets[0].data.push(parseInt(d));
          }
          chartSell.update();
        }
      }
    });
  }

}
