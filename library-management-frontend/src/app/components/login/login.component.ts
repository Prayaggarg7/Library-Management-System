import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {
    // Redirect if already logged in
    if (this.authService.getToken()) {
      this.router.navigate(['/dashboard']);
    }

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  fillCreds(email: string, password: string) {
    this.loginForm.patchValue({ email, password });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        // this.toastr.success('Welcome back!', 'Login successful');
        this.router.navigate(['/dashboard']);
        this.loading = false;
      },
      error: (error) => {
        const message = error.error?.message || 'Invalid email or password';
        this.messageService.add({
          severity: 'error',
          summary: message,
          detail: 'Login failed'
        });
        this.loading = false;
      }
    });
  }
}
