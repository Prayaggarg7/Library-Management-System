export class PageResponse<T> {
  content: T[] = [];
  totalElements: number = 0;
  totalPages: number = 0;
  size: number = 10;
  number: number = 0;
}