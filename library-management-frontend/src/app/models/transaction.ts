export class Transaction {
  id: number = 0;
  bookId: number = 0;
  bookTitle: string = '';
  memberId: number = 0;
  memberName: string = '';
  issuedAt: Date = new Date();
  dueDate: Date = new Date();
  returnedAt: Date | null = null;
  status: string = '';
  fineAmount: number = 0;
}

export class IssueRequest {
  bookId: number = 0;
  memberId: number = 0;
  dueDate: Date | null = null;
}

export class ReturnRequest {
  transactionId: number = 0;
}