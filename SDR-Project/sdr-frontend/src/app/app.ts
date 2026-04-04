import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {

  deviceId = '';
  apiKey = '';
  result = '';

  logs: any[] = [];

  total = 0;
  authorized = 0;
  rejected = 0;

  currentView: 'verify' | 'dashboard' = 'verify';

  constructor(private http: HttpClient) {}

  switchView(view: 'verify' | 'dashboard') {
    this.currentView = view;

    if (view === 'dashboard') {
      this.loadLogs(); // 🔥 load logs when opening dashboard
    }
  }

  verifyDevice() {

    const url = `http://localhost:8080/verify?deviceId=${this.deviceId}&apiKey=${this.apiKey}`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        this.result = res.status + " - " + res.message;

        // 🔥 IMPORTANT: refresh logs after verification
        this.loadLogs();
      },
      error: () => {
        this.result = "ERROR CONNECTING TO SERVER";
      }
    });
  }

  loadLogs() {
    this.http.get<any[]>("http://localhost:8080/logs")
      .subscribe(data => {
        this.logs = data;

        this.total = data.length;
        this.authorized = data.filter(l => l.status === 'AUTHORIZED').length;
        this.rejected = data.filter(l => l.status === 'REJECTED').length;
      });
  }

  ngOnInit() {
    this.loadLogs(); // initial load
  }

}