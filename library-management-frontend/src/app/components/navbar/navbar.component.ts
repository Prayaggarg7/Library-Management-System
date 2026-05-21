import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  userName = '';
  userRole = '';
  private sub: Subscription = new Subscription();

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.sub = this.authService.isAuthenticated$.subscribe(auth => {
      this.isAuthenticated = auth;
      if (auth) {
        const user = this.authService.getUser();
        this.userName = user?.name || user?.email || '';
        this.userRole = user?.role || '';
      } else {
        this.userName = '';
        this.userRole = '';
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  isLibrarian(): boolean {
    return this.authService.isLibrarian();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
