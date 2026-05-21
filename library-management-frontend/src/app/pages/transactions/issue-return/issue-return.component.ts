import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BookService } from '../../../services/book.service';
import { MemberService } from '../../../services/member.service';
import { TransactionService } from '../../../services/transaction.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-issue-return',
  templateUrl: './issue-return.component.html',
  styleUrls: ['./issue-return.component.css']
})
export class IssueReturnComponent implements OnInit {
  issueForm!: FormGroup;
  books: any[] = [];
  members: any[] = [];
  activeTransactions: any[] = [];
  selectedTransactionId: number | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private memberService: MemberService,
    private transactionService: TransactionService,
private messageService: MessageService   ) {}

  ngOnInit() {
    this.initForm();
    this.loadData();
  }

  initForm() {
    this.issueForm = this.fb.group({
      bookId: ['', Validators.required],
      memberId: ['', Validators.required],
      dueDate: [null]
    });
  }

  loadData() {
    this.loading = true;
    this.loadBooks();
    this.loadMembers();
    this.loadActiveTransactions();
  }

  loadBooks() {
    this.bookService.getBooks(0, 100).subscribe({
      next: (response: any) => {
        this.books = response.content.filter((b: any) => b.availableCopies > 0);
        this.loading = false;
      },
      error: (error: any) => {
        this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load books'
          });
        this.loading = false;
      }
    });
  }

  loadMembers() {
    this.memberService.getMembers(0, 100).subscribe({
      next: (response: any) => {
        this.members = response.content.filter((m: any) => m.status === 'ACTIVE');
      },
      error: (error: any) => {
        this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load members'
          });
      }
    });
  }

  loadActiveTransactions() {
    this.transactionService.getAllTransactions(0, 100).subscribe({
      next: (response: any) => {
        this.activeTransactions = response.content.filter((t: any) => t.status === 'ISSUED');
      },
      error: (error: any) => {
        this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load active transactions'
          });
      }
    });
  }

  issueBook() {
    if (this.issueForm.valid) {
      this.loading = true;
      this.transactionService.issueBook(this.issueForm.value).subscribe({
        next: () => {
                      this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: 'Book issued successfully'
});
          this.issueForm.reset();
          this.loadData();
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Failed to issue book'
          });
          this.loading = false;
        }
      });
    }
  }

  returnBook() {
    if (this.selectedTransactionId) {
      this.loading = true;
      this.transactionService.returnBook({ transactionId: this.selectedTransactionId }).subscribe({
        next: () => {
                      this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: 'Book returned successfully'
});
          this.selectedTransactionId = null;
          this.loadData();
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Failed to return book'
          });
          this.loading = false;
        }
      });
    }
  }

  isOverdue(dueDate: Date): boolean {
    return new Date(dueDate) < new Date();
  }
}