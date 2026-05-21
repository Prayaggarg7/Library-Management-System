import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../../../services/transaction.service';

@Component({
  selector: 'app-transaction-history',
  templateUrl: './transaction-history.component.html',
  styleUrls: ['./transaction-history.component.css']
})
export class TransactionHistoryComponent implements OnInit {
  transactions: any[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 1;
  pageSize = 10;
  memberId: number | null = null;
  bookId: number | null = null;
  filterApplied = false;
  loading = false;

  constructor(private transactionService: TransactionService) {}

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions() {
    this.loading = true;
    
    if (this.filterApplied && this.memberId) {
      this.transactionService.getTransactionsByMember(this.memberId, this.currentPage - 1, this.pageSize).subscribe({
        next: (response: any) => {
          this.transactions = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading transactions:', error);
          this.loading = false;
        }
      });
    } else if (this.filterApplied && this.bookId) {
      this.transactionService.getTransactionsByBook(this.bookId, this.currentPage - 1, this.pageSize).subscribe({
        next: (response: any) => {
          this.transactions = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading transactions:', error);
          this.loading = false;
        }
      });
    } else {
      this.transactionService.getAllTransactions(this.currentPage - 1, this.pageSize).subscribe({
        next: (response: any) => {
          this.transactions = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading transactions:', error);
          this.loading = false;
        }
      });
    }
  }

  applyFilters() {
    this.filterApplied = true;
    this.currentPage = 1;
    this.loadTransactions();
  }

  clearFilters() {
    this.memberId = null;
    this.bookId = null;
    this.filterApplied = false;
    this.currentPage = 1;
    this.loadTransactions();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadTransactions();
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'ISSUED': return 'badge bg-warning';
      case 'RETURNED': return 'badge bg-success';
      case 'OVERDUE': return 'badge bg-danger';
      default: return 'badge bg-secondary';
    }
  }

  isOverdue(dueDate: Date): boolean {
    return new Date(dueDate) < new Date();
  }
}