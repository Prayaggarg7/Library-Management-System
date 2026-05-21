import { Component, OnInit } from '@angular/core';
import { BookService } from '../../../services/book.service';
import { MessageService } from 'primeng/api';
@Component({
  selector: 'app-books-list',
  templateUrl: './books-list.component.html',
  styleUrls: ['./books-list.component.css']
})
export class BooksListComponent implements OnInit {
  books: any[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 1;
  pageSize = 10;
  searchQuery = '';
  loading = false;

  constructor(
    private bookService: BookService,
private messageService: MessageService  ) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.loading = true;
    if (this.searchQuery) {
      this.search();
    } else {
      this.bookService.getBooks(this.currentPage - 1, this.pageSize).subscribe({
        next: (response: any) => {
          this.books = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load book'
          });
          this.loading = false;
        }
      });
    }
  }

  search() {
    if (this.searchQuery.trim()) {
      this.loading = true;
      this.bookService.searchBooks(this.searchQuery, this.currentPage - 1, this.pageSize).subscribe({
        next: (response: any) => {
          this.books = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
           this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Search failed'
          });
          this.loading = false;
        }
      });
    } else {
      this.loadBooks();
    }
  }

  clearSearch() {
    this.searchQuery = '';
    this.currentPage = 1;
    this.loadBooks();
  }

  deleteBook(id: number) {
    if (confirm('Are you sure you want to delete this book?')) {
      this.bookService.deleteBook(id).subscribe({
        next: () => {
                      this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: 'Book deleted successfully'
});
          this.loadBooks();
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Failed to delete book'
          });
        }
      });
    }
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadBooks();
  }
}