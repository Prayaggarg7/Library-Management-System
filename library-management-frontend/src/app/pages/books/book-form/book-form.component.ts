import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService } from '../../../services/book.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-book-form',
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.css']
})
export class BookFormComponent implements OnInit {
  bookForm!: FormGroup;
  isEditMode = false;
  bookId: number | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService  ) {}

  ngOnInit() {
    this.initForm();
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.bookId = +params['id'];
        this.loadBook();
      }
    });
  }

  initForm() {
    this.bookForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      author: ['', [Validators.required, Validators.maxLength(255)]],
      isbn: ['', [Validators.required, Validators.pattern(/^[0-9-]{10,20}$/)]],
      category: [''],
      totalCopies: [0, [Validators.required, Validators.min(0)]],
      availableCopies: [0, [Validators.required, Validators.min(0)]],
      shelfLocation: ['']
    });
  }

  loadBook() {
    this.loading = true;
    if (this.bookId) {
      this.bookService.getBookById(this.bookId).subscribe({
        next: (book: any) => {
          this.bookForm.patchValue(book);
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load book'
          });
          this.router.navigate(['/books']);
          this.loading = false;
        }
      });
    }
  }

  onSubmit() {
    if (this.bookForm.valid) {
      this.loading = true;
      const bookData = this.bookForm.value;
      
      const request = this.isEditMode && this.bookId
        ? this.bookService.updateBook(this.bookId, bookData)
        : this.bookService.createBook(bookData);

      request.subscribe({
        next: () => {
          this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: this.isEditMode ? 'Book updated successfully' : 'Book created successfully'
});
          this.router.navigate(['/books']);
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Operation failed'
          });
          this.loading = false;
        }
      });
    } else {
      Object.keys(this.bookForm.controls).forEach(key => {
        this.bookForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel() {
    this.router.navigate(['/books']);
  }
}