import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { BooksListComponent } from './pages/books/books-list/books-list.component';
import { BookFormComponent } from './pages/books/book-form/book-form.component';
import { MembersListComponent } from './pages/members/members-list/members-list.component';
import { MemberFormComponent } from './pages/members/member-form/member-form.component';
import { IssueReturnComponent } from './pages/transactions/issue-return/issue-return.component';
import { TransactionHistoryComponent } from './pages/transactions/transaction-history/transaction-history.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'books', component: BooksListComponent, canActivate: [AuthGuard] },
  { path: 'books/new', component: BookFormComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'books/edit/:id', component: BookFormComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'members', component: MembersListComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'members/new', component: MemberFormComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'members/edit/:id', component: MemberFormComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'issue-return', component: IssueReturnComponent, canActivate: [AuthGuard] },
  { path: 'transactions', component: TransactionHistoryComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }