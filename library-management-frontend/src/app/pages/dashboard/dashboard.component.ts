import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { MemberService } from '../../services/member.service';
import { TransactionService } from '../../services/transaction.service';
import { AuthService } from '../../services/auth.service';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalBooks = 0;
  totalMembers = 0;
  activeIssues = 0;
  recentBooks: any[] = [];
  recentTransactions: any[] = [];
  loading = true;
  userName = '';

  constructor(
    private bookService: BookService,
    private memberService: MemberService,
    private transactionService: TransactionService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const user = this.authService.getUser();
    this.userName = user?.name || user?.email || 'User';
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.loading = true;

    // Use forkJoin so loading = false only after ALL requests finish (or any fails)
    forkJoin({
      books: this.bookService.getBooks(0, 5),
      members: this.memberService.getMembers(0, 1),
      transactions: this.transactionService.getAllTransactions(0, 10)
    }).pipe(
      finalize(() => this.loading = false)   // ALWAYS runs, even on error
    ).subscribe({
      next: (results: any) => {
        this.totalBooks = results.books?.totalElements ?? 0;
        this.recentBooks = results.books?.content ?? [];

        this.totalMembers = results.members?.totalElements ?? 0;

        this.recentTransactions = results.transactions?.content ?? [];
        this.activeIssues = this.recentTransactions.filter(
          (t: any) => t.status === 'ISSUED'
        ).length;
      },
      error: (error: any) => {
        console.error('Dashboard load error:', error);
        // loading = false is handled by finalize
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'ISSUED': return 'status-issued';
      case 'RETURNED': return 'status-returned';
      case 'OVERDUE': return 'status-overdue';
      default: return 'status-default';
    }
  }
}
