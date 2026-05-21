import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api'; 
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { BooksListComponent } from './pages/books/books-list/books-list.component';
import { BookFormComponent } from './pages/books/book-form/book-form.component';
import { MembersListComponent } from './pages/members/members-list/members-list.component';
import { MemberFormComponent } from './pages/members/member-form/member-form.component';
import { IssueReturnComponent } from './pages/transactions/issue-return/issue-return.component';
import { TransactionHistoryComponent } from './pages/transactions/transaction-history/transaction-history.component';
import { StatusColorPipe } from './pipes/status-color.pipe';
import { TruncatePipe } from './pipes/truncate.pipe';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { LoginComponent } from './components/login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    DashboardComponent,
    BooksListComponent,
    BookFormComponent,
    MembersListComponent,
    MemberFormComponent,
    IssueReturnComponent,
    TransactionHistoryComponent,
    StatusColorPipe,
    TruncatePipe
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    ToastModule,
    AppRoutingModule,
  ],
  providers: [
    MessageService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }