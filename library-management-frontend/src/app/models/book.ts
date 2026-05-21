export class Book {
  id?: number;
  title: string = '';
  author: string = '';
  isbn: string = '';
  category: string = '';
  totalCopies: number = 0;
  availableCopies: number = 0;
  shelfLocation: string = '';
  createdAt?: Date;
  updatedAt?: Date;
}